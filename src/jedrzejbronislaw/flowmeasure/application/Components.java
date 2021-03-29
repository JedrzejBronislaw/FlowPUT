package jedrzejbronislaw.flowmeasure.application;

import java.util.Arrays;
import java.util.List;

import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.components.ConnectionService;
import jedrzejbronislaw.flowmeasure.components.SavingService;
import jedrzejbronislaw.flowmeasure.components.SettingsService;
import jedrzejbronislaw.flowmeasure.components.calibration.Calibration;
import jedrzejbronislaw.flowmeasure.components.calibration.Calibration1;
import jedrzejbronislaw.flowmeasure.components.connectionMonitor.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.components.connectionMonitor.ConnectionMonitor1;
import jedrzejbronislaw.flowmeasure.components.dataBuffer.DataBuffer1;
import jedrzejbronislaw.flowmeasure.components.dialogManager.DialogManager;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.components.globalActions.GlobalActions;
import jedrzejbronislaw.flowmeasure.devices.edDevice.EDDevice;
import jedrzejbronislaw.flowmeasure.devices.flowDevice.FlowDevice;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventPolicy;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.InternalResourceAccess;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import jedrzejbronislaw.flowmeasure.view.ViewBuilder;
import jedrzejbronislaw.flowmeasure.view.ViewManager;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import lombok.Getter;

public abstract class Components {
	
	@Getter private static Stage primaryStage;

	@Getter private static ComponentsLoader componentsLoader = new ComponentsLoader();
	@Getter private static ResourceAccess resources;
	@Getter private static UARTDevice flowDevice;
	@Getter private static UARTDevice edDevice;
	@Getter private static FlowManager flowManager;
	@Getter private static Settings settings;
	@Getter private static ConnectionMonitor connectionMonitor;
	@Getter private static EventManager eventManager;
	@Getter private static StateManager stateManager;
	@Getter private static EventPolicy eventPolicy;
	@Getter private static DialogManager dialogManager;
	@Getter private static FlowConverters flowConverters;
	@Getter private static Calibration calibration;
	@Getter private static ViewMediator viewMediator;
	@Getter private static Repository repository;
	@Getter private static GlobalActions globalActions;
	@Getter private static ViewManager viewManager;
	
	@Getter private static ConnectionService connectionService;
	@Getter private static SavingService savingService;
	@Getter private static SettingsService settingsService;
	
	
	public static void create(Stage stage) {
		primaryStage = stage;
		
		resources = new InternalResourceAccess().build();
		
		settings = new Settings();
		viewMediator = new ViewMediator();
		flowDevice = buildFlowDevice();
		edDevice = buildEDDevice();
		connectionMonitor = buildConnectionMonitor();
		flowConverters = new FlowConverters(Consts.FLOWMETERS_NUMBER);
		eventManager = new EventManager();
		stateManager = new StateManager();
		eventPolicy = new EventPolicy();
		dialogManager = buildDialogManager();
		calibration = buildCalibration();
		repository = new Repository();
		flowManager = new FlowManager();
		
		connectionService = new ConnectionService();
		savingService = new SavingService();
		settingsService = new SettingsService();
		
		globalActions = new GlobalActions();
		viewManager = new ViewManager();
		
		componentsLoader.load();
		set();
	}

	private static void set() {
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
		eventManager.addListener(flowConverters);

		new ViewBuilder().build();
	}
	
	private static FlowDevice buildFlowDevice() {
		FlowDevice device = new FlowDevice();

		device.setNewSingleFlowReceive(viewMediator::showCurrentFlow);
		
		device.setNewFlowsReceive(flows -> {
			eventManager.submitEvent(EventType.RECEIVED_DATA);
			flowManager.addFlowMeasurement(flows);
		});

		return device;
	}
	
	private static EDDevice buildEDDevice() {
		EDDevice device = new EDDevice();

		device.setNewSingleFlowReceive(viewMediator::showCurrentFlow);
		
		device.setNewFlowsReceive(flows -> {
			eventManager.submitEvent(EventType.RECEIVED_DATA);
			flowManager.addFlowMeasurement(flows);
		});

		return device;
	}
	
	private static ConnectionMonitor buildConnectionMonitor() {
		ConnectionMonitor1 monitor = new ConnectionMonitor1(3, 1000, () -> {
			getDevices().forEach(device -> device.disconnect());
			eventManager.submitEvent(EventType.LOST_CONNECTION);
		});
		
		return monitor;
	}

	private static DialogManager buildDialogManager() {
		return new DialogManager.builder().
				addMessages(EventType.CONNECTION_SUCCESSFUL, "Connection to FlowPUT device established").
				addMessages(EventType.LOST_CONNECTION, "FlowPUT device has been disconnected").
				addMessages(EventType.CONNECTION_FAILED, "Connection to FlowPUT device failed").
				build();
	}

	private static Calibration buildCalibration() {
		Calibration calibration = new Calibration1();
		calibration.setVolume(1);
		calibration.reset();
		
		return calibration;
	}

	public static List<UARTDevice> getDevices() {
		return Arrays.asList(edDevice, flowDevice);
	}
}
