package jedrzejbronislaw.flowmeasure.application;

import java.time.LocalDateTime;

import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.FlowConverter1;
import jedrzejbronislaw.flowmeasure.FlowDevice;
import jedrzejbronislaw.flowmeasure.FlowManager;
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.SideDirResourcesRepository;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventPolicy;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.Calibration1;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.services.ConnectionMonitor1;
import jedrzejbronislaw.flowmeasure.services.DataBuffer1;
import jedrzejbronislaw.flowmeasure.services.DialogManager;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.view.Actions;
import jedrzejbronislaw.flowmeasure.view.ViewBuilder;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import lombok.Getter;

@Getter
public class Components {

	private Stage primaryStage;

	private ResourcesRepository resources;
	private FlowDevice device;
	private FlowManager flowManager;
	private Settings settings;
	private ConnectionMonitor connectionMonitor;
	private EventManager eventManager;
	private StateManager stateManager;
	private EventPolicy eventPolicy;
	private DialogManager dialogManager;
	private FlowConverter flowConverter;
	private Calibration calibration;
	private ViewMediator viewMediator;
	private Repository repository;
	
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
		repository = new Repository();
		flowManager = new FlowManager(repository);
		
		set();
	}

	private void set() {
		settings.loadFromFile();
		
		flowManager.setBufferCreator(() ->
			new DataBuffer1(
				repository.getCurrentProcessRepository(),
				settings.getInt(AppProperties.BUFFER_INTERVAL)
			));
		flowManager.setCalibration(calibration);

		eventManager.setEventPolicy(eventPolicy);
		eventManager.addListener(dialogManager);
		eventManager.addListener(stateManager);
		eventManager.addListener(connectionMonitor);
		eventManager.addListener(flowConverter);

		MyFXMLLoader.setResources(resources);
		createViewBuilder().build();
	}
	
	private FlowDevice buildFlowDevice() {
		FlowDevice device = new FlowDevice();

		device.setNewSingleFlowReceive(viewMediator::showCurrentFlow);
		
		device.setNewFlowsReceive(flows -> {
			eventManager.submitEvent(EventType.ReceivedData);
			flowManager.addFlowMeasurement(flows);
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
		Calibration calibration = new Calibration1();
		calibration.setVolume(1);
		calibration.reset();
		
		return calibration;
	}

	private ViewBuilder createViewBuilder(){
		return new ViewBuilder(this, new Actions(this));
	}
}
