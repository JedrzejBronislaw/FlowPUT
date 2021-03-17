package jedrzejbronislaw.flowmeasure.application;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.components.calibration.Calibration;
import jedrzejbronislaw.flowmeasure.components.calibration.Calibration1;
import jedrzejbronislaw.flowmeasure.components.connectionMonitor.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.components.connectionMonitor.ConnectionMonitor1;
import jedrzejbronislaw.flowmeasure.components.dataBuffer.DataBuffer1;
import jedrzejbronislaw.flowmeasure.components.dialogManager.DialogManager;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.edDevice.EDDevice;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventPolicy;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.flowDevice.FlowDevice;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.InternalResourceAccess;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import jedrzejbronislaw.flowmeasure.view.Actions;
import jedrzejbronislaw.flowmeasure.view.ViewBuilder;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import lombok.Getter;

@Getter
public class Components {
	
	private Stage primaryStage;

	private ResourceAccess resources;
	private UARTDevice flowDevice;
	private UARTDevice edDevice;
	private FlowManager flowManager;
	private Settings settings;
	private ConnectionMonitor connectionMonitor;
	private EventManager eventManager;
	private StateManager stateManager;
	private EventPolicy eventPolicy;
	private DialogManager dialogManager;
	private FlowConverters flowConverters;
	private Calibration calibration;
	private ViewMediator viewMediator;
	private Repository repository;
	
	public Components(Stage stage) {
		primaryStage = stage;
		
		resources = new InternalResourceAccess().build();
		
		settings = new Settings();
		viewMediator = new ViewMediator();
		flowDevice = buildFlowDevice();
		edDevice = buildEDDevice();
		connectionMonitor = buildConnectionMonitor();
		flowConverters = new FlowConverters(settings, Consts.FLOWMETERS_NUMBER);
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
		eventManager.addListener(flowConverters);

		MyFXMLLoader.setResources(resources);
		createViewBuilder().build();
	}
	
	private UARTDevice buildDevice(UARTDevice device) {
		device.setIncorrectMessageReceive(m -> System.out.println("(" + LocalDateTime.now().toString() + ") Incorrect Message: " + m));
		device.setDeviceConfirmation(() -> System.out.println("Device confirmation"));
			
		return device;
	}
	
	private FlowDevice buildFlowDevice() {
		FlowDevice device = new FlowDevice();
		buildDevice(device);

		device.setNewSingleFlowReceive(viewMediator::showCurrentFlow);
		
		device.setNewFlowsReceive(flows -> {
			eventManager.submitEvent(EventType.RECEIVED_DATA);
			flowManager.addFlowMeasurement(flows);
		});

		return device;
	}
	
	private EDDevice buildEDDevice() {
		EDDevice device = new EDDevice();
		buildDevice(device);

		device.setNewSingleFlowReceive(viewMediator::showCurrentFlow);
		
		device.setNewFlowsReceive(flows -> {
			eventManager.submitEvent(EventType.RECEIVED_DATA);
			flowManager.addFlowMeasurement(flows);
		});

		return device;
	}
	
	private ConnectionMonitor buildConnectionMonitor() {
		ConnectionMonitor1 monitor = new ConnectionMonitor1(3, 1000, () -> {
			getDevices().forEach(device -> device.disconnect());
			eventManager.submitEvent(EventType.LOST_CONNECTION);
		});
		
		return monitor;
	}

	private DialogManager buildDialogManager() {
		return new DialogManager.builder().
				addMessages(EventType.CONNECTION_SUCCESSFUL, "Connection to FlowPUT device established").
				addMessages(EventType.LOST_CONNECTION, "FlowPUT device has been disconnected").
				addMessages(EventType.CONNECTION_FAILED, "Connection to FlowPUT device failed").
				build();
	}

	private Calibration buildCalibration() {
		Calibration calibration = new Calibration1();
		calibration.setVolume(1);
		calibration.reset();
		
		return calibration;
	}

	public List<UARTDevice> getDevices() {
		return Arrays.asList(edDevice, flowDevice);
	}
	
	private ViewBuilder createViewBuilder(){
		return new ViewBuilder(this, new Actions(this));
	}
}
