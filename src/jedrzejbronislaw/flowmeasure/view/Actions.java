package jedrzejbronislaw.flowmeasure.view;

import jedrzejbronislaw.flowmeasure.ConnectionAttempt;
import jedrzejbronislaw.flowmeasure.ConnectionsAttempts;
import jedrzejbronislaw.flowmeasure.FileNamer;
import jedrzejbronislaw.flowmeasure.FileNamer1;
import jedrzejbronislaw.flowmeasure.FlowDevice;
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Session.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.UART;
import jedrzejbronislaw.flowmeasure.UARTParams;
import jedrzejbronislaw.flowmeasure.events.EventManager1;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryCSVWriter;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriter;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.view.view1.SaveWindowBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Actions implements ActionContainer {
	
	@NonNull private Session session;
	@NonNull private EventManager1 eventManager;
	@NonNull private Settings settings;
	@NonNull private ResourcesRepository resources;
	@NonNull private View view;
	@NonNull private ConnectionMonitor connectionMonitor;
	@NonNull private FlowDevice device;
	
	@Override
	public void startProcess() {
		if(eventManager.submitEvent(EventType.Process_Starts)) {
//			session.getCurrentProcessRepository().setProcessStartTimeNow();
			session.getCurrentProcessRepository().setProcessStartTimeWithNextValue();
			
			if(settings.isBufferedData()) {
				session.setFlowConsumerType(FlowConsumerType.Buffered);
				session.setBufferInterval(settings.getBufferInterval());
			} else
				session.setFlowConsumerType(FlowConsumerType.Plain);
				
		}
	}

	@Override
	public void endProcess() {
		if(eventManager.submitEvent(EventType.Process_Ends)) {
			session.getCurrentProcessRepository().setProcessEndTimeNow();
			session.setFlowConsumerType(FlowConsumerType.None);
		}
	}

	@Override
	public void saveProcess() {
		if(eventManager.submitEvent(EventType.Saving_Process)) {
			ProcessRepositoryWriter writer = new ProcessRepositoryCSVWriter();
			ProcessRepository process = session.getCurrentProcessRepository();
			SaveWindowBuilder builder = new SaveWindowBuilder(resources, process);
				
			writer.setPulsePerLitre(settings.getPulsePerLitre());
			FileNamer filenamer = new FileNamer1(process);
			builder.setFileNamer(filenamer::createName);
			builder.setInitialDirectory(settings.getSavePath());					
			builder.setSaveAction(writer::save);
			builder.setChooseFileAction(file -> {
				settings.setSavePath(file.getParent());
				settings.write();
			});
				
			builder.build().show();					
		}
	}

	@Override
	public void connectFlowDevice() {
		UARTParams params = view.getUARTParams();
		
		if (params == null) return;
		if (params.PORT_NAME == null || params.PORT_NAME.isEmpty()) return;

		
		ConnectionAttempt attempt = new ConnectionAttempt(device, params);
		attempt.setSuccess(() -> {
			eventManager.submitEvent(EventType.ConnectionSuccessful);
			connectionMonitor.start();
		});
		attempt.setFail(() -> {
			eventManager.submitEvent(EventType.ConnectionFailed);
		});

		eventManager.submitEvent(EventType.Connecting_Start);
		attempt.start();
	}

	@Override
	public void disconnectFlowDevice() {
		connectionMonitor.stop();
		device.disconnect();

		eventManager.submitEvent(EventType.Diconnection);
	}

	@Override
	public void autoconnectFlowDevice() {
		System.out.println("\nStart autoconnect");
		
		ConnectionsAttempts attempts = new ConnectionsAttempts(device, UART.getPortList(), 9600);
		attempts.setFail(() -> {
			System.out.println("¯aden port nie pasuje");
			eventManager.submitEvent(EventType.ConnectionFailed);
		});
		attempts.setSuccess(port -> {
			System.out.println("Uda³o po³¹czyæ siê z portem: " + port);
			eventManager.submitEvent(EventType.ConnectionSuccessful);
			connectionMonitor.start();
		});

		eventManager.submitEvent(EventType.Connecting_Start);
		attempts.start();
	}

	@Override
	public void exit() {
		eventManager.submitEvent(EventType.Exiting);
		connectionMonitor.stop();
		device.disconnect();
	}
}
