package jedrzejbronislaw.flowmeasure.application;
	
import java.time.LocalDateTime;

import javafx.application.Application;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.ConnectionAttempt;
import jedrzejbronislaw.flowmeasure.ConnectionsAttempts;
import jedrzejbronislaw.flowmeasure.EventPolicy;
import jedrzejbronislaw.flowmeasure.FakeProcessGenerator1;
import jedrzejbronislaw.flowmeasure.FileNamer;
import jedrzejbronislaw.flowmeasure.FileNamer1;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.FlowConverter1;
import jedrzejbronislaw.flowmeasure.FlowDevice;
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Session.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.SideDirResourcesRepository;
import jedrzejbronislaw.flowmeasure.StateManager;
import jedrzejbronislaw.flowmeasure.UART;
import jedrzejbronislaw.flowmeasure.UARTParams;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryCSVWriter;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriter;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.Calibration1;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor1;
import jedrzejbronislaw.flowmeasure.services.DataBuffer;
import jedrzejbronislaw.flowmeasure.services.DataBuffer1;
import jedrzejbronislaw.flowmeasure.services.DialogManager1;
import jedrzejbronislaw.flowmeasure.services.EventManager1;
import jedrzejbronislaw.flowmeasure.services.EventType;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.view.View;
import jedrzejbronislaw.flowmeasure.view.ViewBuilder;
import jedrzejbronislaw.flowmeasure.view.view1.SaveWindowBuilder;
import jedrzejbronislaw.flowmeasure.view.view1.ViewBuilder1;


public class Main extends Application {

	private final static int FLOWMETERS_NUMBER = 6;
	
	private ResourcesRepository resources;
	private FlowDevice device;
	private Session session;
	private Settings settings;
	private ConnectionMonitor connectionMonitor;
	private EventManager1 eventManager;
	private StateManager stateManager;
	private EventPolicy eventPolicy;
	private DialogManager1 dialogManager;
	private FlowConverter flowConverter;
	private DataBuffer dataBuffer;
	private Calibration calibration;
	private View view;
	private Stage primaryStage;
	

	
	
	@Override
	public void start(Stage stage) {
		
		
		resources = new SideDirResourcesRepository("res");
		
		
		primaryStage = stage;
		settings = new Settings();
		device = buildFlowDevice();
		connectionMonitor = buildConnectionMonitor();
		flowConverter = new FlowConverter1(settings);
		eventManager = new EventManager1();
		stateManager = new StateManager();
		eventPolicy = new EventPolicy(stateManager);
		dialogManager = buildDialogManager();		
		calibration = buildCalibration();
		session = buildSession();
		
		settings.read();
		
		
		session.setRepository(new Repository());
		session.setCurrentProcessRepository(createProcessRepository(session.getRepository()));
		session.setDevice(device);

		eventManager.setEventPolicy(eventPolicy);
		eventManager.addListener(dialogManager);
		eventManager.addListener(stateManager);


		dataBuffer = new DataBuffer1(session.getCurrentProcessRepository(), 1000);

		MyFXMLLoader.setResources(resources);
		ViewBuilder viewBuilder = createViewBuilder();
		view = viewBuilder.build();

//		settings.setBufferedFlowConsumer(dataBuffer);
//		settings.setPlainFlowConsumer(session.getCurrentProcessRepository());
//		settings.setPlainFlowMeasurementConsumer();
		session.setBufferedFlowConsumer(dataBuffer);
		session.setPlainFlowConsumer(session.getCurrentProcessRepository());
//		session.setPlainFlowMeasurementConsumer();
		session.setCalibration(calibration);
		
		
		//-----
		
		session.getAppState().addStateListiner(state -> System.out.println(" -> New appState: " + state.toString()));
		session.getConnState().addStateListiner(state -> System.out.println(" -> New connState: " + state.toString()));
		session.getProcessState().addStateListiner(state -> System.out.println(" -> New processState: " + state.toString()));
		eventManager.addListener(state -> {if(state != EventType.ReceivedData) System.out.println(" -> Event: " + state.toString());});
		
//		generateFakeData();
	}
	
	private void generateFakeData() {
		FakeProcessGenerator1 generator = new FakeProcessGenerator1();
		generator.setInterval(1000);
		generator.setNumOfFlowmeters(6);
		
		generator.generate(session.getCurrentProcessRepository(), 10*60*60);
	}

	private Session buildSession() {
		return Session.builder().
				setAppState(stateManager.getAppState()).
				setConnState(stateManager.getConnState()).
				setProcessState(stateManager.getProcessState()).
				build();
	}

	private Calibration buildCalibration() {
		Calibration calibration = new Calibration1(1);
		calibration.setVolume(1);
		calibration.reset();
		
		return calibration;
	}

	private DialogManager1 buildDialogManager() {
		return new DialogManager1.builder().
				addMessages(EventType.ConnectionSuccessful, "Nawiązano połączenie z urządzeniem FlowPP").
				addMessages(EventType.LostConnection, "Utracono połączenie z urządzeniem FlowPP").
				addMessages(EventType.ConnectionFailed, "Nie udało się nawiązać połączenia z urządzeniem FLowPP").
				setShowMessage((title, content, closeDelay) -> view.showDialog(title, content, closeDelay)).
				build();
	}

	private ConnectionMonitor buildConnectionMonitor() {
		ConnectionMonitor1 monitor = new ConnectionMonitor1(3, 1000, () -> {
			System.out.println("TIMEOUT");
//			session.setProcessState(ProcessState.LostConnection);
//			view.disconnected();
			device.disconnect();
			eventManager.submitEvent(EventType.LostConnection);
		});
		
		return monitor;
	}
	
	private ViewBuilder createViewBuilder(){
		ViewBuilder1 viewBuilder = new ViewBuilder1(primaryStage, session, settings);

		viewBuilder.setResources(resources);
		viewBuilder.setFlowconverter(flowConverter);
		viewBuilder.setEventManager(eventManager);
		viewBuilder.setCalibration(calibration);

		viewBuilder.getActions().setStartButton(() -> {
			if(eventManager.submitEvent(EventType.Process_Starts)) {
//				session.getCurrentProcessRepository().setProcessStartTimeNow();
				session.getCurrentProcessRepository().setProcessStartTimeWithNextValue();
				
				if(settings.isBufferedData()) {
					session.setFlowConsumerType(FlowConsumerType.Buffered);
					session.setBufferInterval(settings.getBufferInterval());
				} else
					session.setFlowConsumerType(FlowConsumerType.Plain);
					
			}
		});
		viewBuilder.getActions().setEndButton(() -> {
			if(eventManager.submitEvent(EventType.Process_Ends)) {
				session.getCurrentProcessRepository().setProcessEndTimeNow();
				session.setFlowConsumerType(FlowConsumerType.None);
			}
		});
		viewBuilder.getActions().setSaveButton(() -> {
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
		});
		
		viewBuilder.getActions().setConnectButton(() -> {
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
		});
		
		viewBuilder.getActions().setAutoconnectButton(() -> {
			System.out.println("\nStart autoconnect");
			
			ConnectionsAttempts attempts = new ConnectionsAttempts(device, UART.getPortList(), 9600);
			attempts.setFail(() -> {
				System.out.println("Żaden port nie pasuje");
				eventManager.submitEvent(EventType.ConnectionFailed);
			});
			attempts.setSuccess(port -> {
				System.out.println("Udało połączyć się z portem: " + port);
				eventManager.submitEvent(EventType.ConnectionSuccessful);
				connectionMonitor.start();
			});

			eventManager.submitEvent(EventType.Connecting_Start);
			attempts.start();
			
		});
		
		viewBuilder.getActions().setDisconnectButton(() -> {
			connectionMonitor.stop();
			device.disconnect();

			eventManager.submitEvent(EventType.Diconnection);
		});
		
		viewBuilder.getActions().setExit(() -> {
			eventManager.submitEvent(EventType.Exiting);
			connectionMonitor.stop();
			device.disconnect();
		});
		
		return viewBuilder;
	}
	
	public static void main(String[] args) {
		launch(args);		
	}
	
	private ProcessRepository createProcessRepository(Repository repository) {
		ProcessRepository processRepo = repository.createNewProcessRepository(FLOWMETERS_NUMBER, "untitled");
		processRepo.getMetadata().setAuthor("unknown");
		
		return processRepo;
	}
	
	private FlowDevice buildFlowDevice() {
		FlowDevice device = new FlowDevice();

		
		device.setNewSingleFlowReceive((flow, nr) -> {
			view.showCurrentFlow(nr, flow);
		});
		
		device.setNewFlowsReceive(flows -> {
			eventManager.submitEvent(EventType.ReceivedData);
			flowConverter.newDataEvent();
			connectionMonitor.newMessage();

			session.getFlowConsumer().addFlowMeasurement(flows);
		});

		device.setIncorrectMessageReceive(m -> System.out.println("(" + LocalDateTime.now().toString() + ") Incorrect Message: " + m));
		device.setDeviceConfirmation(() -> System.out.println("Device confirmation"));		
			
		return device;
	}
	

}
