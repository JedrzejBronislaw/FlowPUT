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
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.builders.SaveWindowBuilder;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryCSVWriter;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriter;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Actions implements ActionContainer {
	
	private final Components components;


	@Override
	public void startProcess() {
		if(eventManager().submitEvent(EventType.Process_Starts)) {
			session().createNewProcessRepository("untitled");
			session().getCurrentProcessRepository().setStartWithNextValueFlag();
			
			if(settings().isBufferedData())
				session().setFlowConsumerType(FlowConsumerType.Buffered);
			else
				session().setFlowConsumerType(FlowConsumerType.Plain);
		}
	}

	@Override
	public void endProcess() {
		if(eventManager().submitEvent(EventType.Process_Ends)) {
			session().getCurrentProcessRepository().setProcessEndTimeNow();
			session().setFlowConsumerType(FlowConsumerType.None);
		}
	}

	@Override
	public void saveProcess() {
		if(eventManager().submitEvent(EventType.Saving_Process)) {
			ProcessRepositoryWriter writer = new ProcessRepositoryCSVWriter();
			ProcessRepository process = session().getCurrentProcessRepository();
			SaveWindowBuilder builder = new SaveWindowBuilder(resources(), process);
				
			writer.setPulsePerLitre(settings().getPulsePerLitre());
			FileNamer filenamer = new FileNamer1(process);
			builder.setFileNamer(filenamer::createName);
			builder.setInitialDirectory(settings().getSavePath());
			builder.setSaveAction(writer::save);
			builder.setOnFileChoose(file -> {
				settings().setSavePath(file.getParent());
				settings().write();
			});
				
			builder.build();
			builder.showWindow();
		}
	}
	
	@Override
	public void closeProcess() {
		eventManager().submitEvent(EventType.Close_Process);
	}

	@Override
	public void connectFlowDevice() {
		UARTParams params = viewMediator().getUARTParams();
		
		if (params == null) return;
		if (params.PORT_NAME == null || params.PORT_NAME.isEmpty()) return;

		
		ConnectionAttempt attempt = new ConnectionAttempt(device(), params);
		attempt.setSuccess(() -> {
			eventManager().submitEvent(EventType.ConnectionSuccessful);
			connectionMonitor().start();
		});
		attempt.setFail(() -> {
			eventManager().submitEvent(EventType.ConnectionFailed);
		});

		eventManager().submitEvent(EventType.Connecting_Start);
		attempt.start();
	}

	@Override
	public void disconnectFlowDevice() {
		connectionMonitor().stop();
		device().disconnect();

		eventManager().submitEvent(EventType.Diconnection);
	}

	@Override
	public void autoconnectFlowDevice() {
		System.out.println("\nStart autoconnect");
		
		ConnectionsAttempts attempts = new ConnectionsAttempts(device(), UART.getPortList(), 9600);
		attempts.setFail(() -> {
			System.out.println("¯aden port nie pasuje");
			eventManager().submitEvent(EventType.ConnectionFailed);
		});
		attempts.setSuccess(port -> {
			System.out.println("Uda³o po³¹czyæ siê z portem: " + port);
			eventManager().submitEvent(EventType.ConnectionSuccessful);
			connectionMonitor().start();
		});

		eventManager().submitEvent(EventType.Connecting_Start);
		attempts.start();
	}

	@Override
	public void exit() {
		eventManager().submitEvent(EventType.Exiting);
		connectionMonitor().stop();
		device().disconnect();
	}
	
	
	
	private EventManager eventManager() {
		return components.getEventManager();
	}
	
	private ConnectionMonitor connectionMonitor() {
		return components.getConnectionMonitor();
	}
	
	private FlowDevice device() {
		return components.getDevice();
	}
	
	private ViewMediator viewMediator() {
		return components.getViewMediator();
	}
	
	private ResourcesRepository resources() {
		return components.getResources();
	}
	
	private Session session() {
		return components.getSession();
	}
	
	private Settings settings() {
		return components.getSettings();
	}
}
