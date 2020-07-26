package jedrzejbronislaw.flowmeasure.view;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.connectionMonitor.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.flowDevice.FlowDevice;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryCSVWriter;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriter;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.fileNamer.FileNamer;
import jedrzejbronislaw.flowmeasure.tools.fileNamer.FileNamer1;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.tools.uart.UART;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.AutoConnection;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.ConnectionAttempt;
import jedrzejbronislaw.flowmeasure.view.saveWindow.SaveWindowBuilder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Actions implements ActionContainer {
	
	private final Components components;


	@Override
	public void startProcess() {
		if(eventManager().submitEvent(EventType.Process_Starts)) {
			repository().createNewProcessRepository("untitled").setStartWithNextValueFlag();
			
			if(isBufferedData())
				flowManager().setFlowConsumerType(FlowConsumerType.Buffered); else
				flowManager().setFlowConsumerType(FlowConsumerType.Plain);
		}
	}

	@Override
	public void endProcess() {
		if(eventManager().submitEvent(EventType.Process_Ends)) {
			repository().getCurrentProcessRepository().setProcessEndTimeNow();
			flowManager().setFlowConsumerType(FlowConsumerType.None);
		}
	}

	@Override
	public void saveProcess() {
		if(eventManager().submitEvent(EventType.Saving_Process))
			showSaveWindow(prepareWriter());
	}
	
	@Override
	public void closeProcess() {
		if (confirmWithAlert())
			eventManager().submitEvent(EventType.Close_Process);
	}

	@Override
	public void connectFlowDevice() {
		UARTParams params = viewMediator().getUARTParams();
		if (!valideteParams(params)) return;
		
		ConnectionAttempt attempt = createConnectionAttempt(params);
		eventManager().submitEvent(EventType.Connecting_Start);
		
		attempt.start();
	}

	@Override
	public void disconnectFlowDevice() {
		connectionMonitor().stop();
		device().disconnect();

		eventManager().submitEvent(EventType.Disconnection);
	}

	@Override
	public void autoconnectFlowDevice() {
		System.out.println("\nStart autoconnect");
		
		AutoConnection autoConnection = createAutoConnection();

		eventManager().submitEvent(EventType.Connecting_Start);
		autoConnection.start();
	}

	@Override
	public void exit() {
		eventManager().submitEvent(EventType.Exiting);
		connectionMonitor().stop();
		device().disconnect();
	}
	
	private ProcessRepositoryWriter prepareWriter() {
		ProcessRepositoryWriter writer = new ProcessRepositoryCSVWriter();
		
		if(isBufferedData())
			writer.setBufferInterval(settings().getInt(AppProperties.BUFFER_INTERVAL));
		writer.setPulsePerLitre(getPulseRatios());
		
		return writer;
	}

	private void showSaveWindow(ProcessRepositoryWriter writer) {
		ProcessRepository process = repository().getCurrentProcessRepository();
		SaveWindowBuilder builder = new SaveWindowBuilder(resources(), process);

		FileNamer filenamer = new FileNamer1(process);
		builder.setOwner(components.getPrimaryStage());
		builder.setFileNamer(filenamer::createName);
		builder.setInitialDirectory(settings().getString(AppProperties.SAVE_PATH));
		builder.setSaveAction(writer::save);
		builder.setOnFileChoose(file -> {
			settings().setProperty(AppProperties.SAVE_PATH, file.getParent());
			settings().saveToFile();
		});
			
		builder.build();
		builder.showWindow();
	}

	private float[] getPulseRatios() {
		RatioProperty[] ratioProperties = RatioProperty.generate(Consts.FLOWMETERS_NUMBER);
		float[] outcome = new float[Consts.FLOWMETERS_NUMBER];
		
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			outcome[i] = settings().getFloat(ratioProperties[i]);
		
		return outcome;
	}

	private boolean confirmWithAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close the process");
		alert.setHeaderText("Are you sure you want to close the process?");
		alert.setContentText("The measurement will be lost.");
		
		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get() == ButtonType.OK;
	}

	private boolean valideteParams(UARTParams params) {
		if (params == null) return false;
		if (params.PORT_NAME == null || params.PORT_NAME.isEmpty()) return false;
		
		return true;
	}

	private ConnectionAttempt createConnectionAttempt(UARTParams params) {
		ConnectionAttempt attempt = new ConnectionAttempt(device(), params);
		
		attempt.setSuccess(() -> {
			eventManager().submitEvent(EventType.ConnectionSuccessful);
			connectionMonitor().start();
		});
		
		attempt.setFail(reason ->
			eventManager().submitEvent(EventType.ConnectionFailed)
		);
		
		return attempt;
	}

	private AutoConnection createAutoConnection() {
		AutoConnection autoConn = new AutoConnection(device(), UART.getPortList(), 9600);
		
		autoConn.setIfFail(() -> {
			System.out.println("¯aden port nie pasuje");
			eventManager().submitEvent(EventType.ConnectionFailed);
		});
		
		autoConn.setIfSuccess(port -> {
			System.out.println("Uda³o po³¹czyæ siê z portem: " + port);
			eventManager().submitEvent(EventType.ConnectionSuccessful);
			connectionMonitor().start();
		});
		
		return autoConn;
	}
	

	private boolean isBufferedData() {
		return settings().getBool(AppProperties.BUFFERED_DATA);
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
	
	private ResourceAccess resources() {
		return components.getResources();
	}
	
	private FlowManager flowManager() {
		return components.getFlowManager();
	}
	
	private Settings settings() {
		return components.getSettings();
	}
	
	private Repository repository() {
		return components.getRepository();
	}
}
