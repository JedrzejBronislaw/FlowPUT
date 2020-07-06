package jedrzejbronislaw.flowmeasure.application;

import java.time.LocalDateTime;

import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.FlowConverter1;
import jedrzejbronislaw.flowmeasure.FlowDevice;
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.SideDirResourcesRepository;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventPolicy;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.Calibration1;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor1;
import jedrzejbronislaw.flowmeasure.services.DataBuffer;
import jedrzejbronislaw.flowmeasure.services.DataBuffer1;
import jedrzejbronislaw.flowmeasure.services.DialogManager;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.Actions;
import jedrzejbronislaw.flowmeasure.view.ViewBuilder;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import lombok.Getter;

@Getter
public class Components {

	private final static int FLOWMETERS_NUMBER = 6;

	private Stage primaryStage;

	private ResourcesRepository resources;
	private FlowDevice device;
	private Session session;
	private Settings settings;
	private ConnectionMonitor connectionMonitor;
	private EventManager eventManager;
	private StateManager stateManager;
	private EventPolicy eventPolicy;
	private DialogManager dialogManager;
	private FlowConverter flowConverter;
	private DataBuffer dataBuffer;
	private Calibration calibration;
	private ViewMediator viewMediator;
	
	public Components(Stage stage) {
		primaryStage = stage;
		
		resources = new SideDirResourcesRepository("res");
		
		settings = new Settings();
		viewMediator = new ViewMediator();
		device = buildFlowDevice();
		connectionMonitor = buildConnectionMonitor();
		flowConverter = new FlowConverter1(settings);
		eventManager = new EventManager();
		stateManager = new StateManager();
		eventPolicy = new EventPolicy(stateManager);
		dialogManager = buildDialogManager();
		calibration = buildCalibration();
		session = new Session();
		
		set();
	}

	private void set() {
		settings.read();
		
		session.setRepository(new Repository());
		session.setCurrentProcessRepository(createProcessRepository(session.getRepository()));
		session.setDevice(device);

		eventManager.setEventPolicy(eventPolicy);
		eventManager.addListener(dialogManager);
		eventManager.addListener(stateManager);
		eventManager.addListener(connectionMonitor);
		eventManager.addListener(flowConverter);


		dataBuffer = new DataBuffer1(session.getCurrentProcessRepository(), 1000);

		MyFXMLLoader.setResources(resources);
		createViewBuilder().build();

//		settings.setBufferedFlowConsumer(dataBuffer);
//		settings.setPlainFlowConsumer(session.getCurrentProcessRepository());
//		settings.setPlainFlowMeasurementConsumer();
		session.setBufferedFlowConsumer(dataBuffer);
		session.setPlainFlowConsumer(session.getCurrentProcessRepository());
//		session.setPlainFlowMeasurementConsumer();
		session.setCalibration(calibration);
	}
	
	private FlowDevice buildFlowDevice() {
		FlowDevice device = new FlowDevice();

		device.setNewSingleFlowReceive(viewMediator::showCurrentFlow);
		
		device.setNewFlowsReceive(flows -> {
			eventManager.submitEvent(EventType.ReceivedData);
			session.getFlowConsumer().addFlowMeasurement(flows);
		});

		device.setIncorrectMessageReceive(m -> System.out.println("(" + LocalDateTime.now().toString() + ") Incorrect Message: " + m));
		device.setDeviceConfirmation(() -> System.out.println("Device confirmation"));
			
		return device;
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

	private DialogManager buildDialogManager() {
		return new DialogManager.builder().
				addMessages(EventType.ConnectionSuccessful, "Nawi¹zano po³¹czenie z urz¹dzeniem FlowPP").
				addMessages(EventType.LostConnection, "Utracono po³¹czenie z urz¹dzeniem FlowPP").
				addMessages(EventType.ConnectionFailed, "Nie uda³o siê nawi¹zaæ po³¹czenia z urz¹dzeniem FLowPP").
				build();
	}

	private Calibration buildCalibration() {
		Calibration calibration = new Calibration1(1);
		calibration.setVolume(1);
		calibration.reset();
		
		return calibration;
	}

	private ViewBuilder createViewBuilder(){
		ViewBuilder viewBuilder = new ViewBuilder(primaryStage, session, settings);
		ActionContainer actions = new Actions(this);

		viewBuilder.setResources(resources);
		viewBuilder.setFlowconverter(flowConverter);
		viewBuilder.setEventManager(eventManager);
		viewBuilder.setStateManager(stateManager);
		viewBuilder.setDialogManager(dialogManager);
		viewBuilder.setCalibration(calibration);
		viewBuilder.setViewMediator(viewMediator);
		viewBuilder.setActions(actions);
		
		return viewBuilder;
	}
	
	private ProcessRepository createProcessRepository(Repository repository) {
		ProcessRepository processRepo = repository.createNewProcessRepository(FLOWMETERS_NUMBER, "untitled");
		processRepo.getMetadata().setAuthor("unknown");
		
		return processRepo;
	}
}
