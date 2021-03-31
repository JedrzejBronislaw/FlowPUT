package jedrzejbronislaw.flowmeasure.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.connectionMonitor.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.devices.DeviceType;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.tools.uart.UART;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.AutoConnection;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.ConnectionAttempt;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.MultiDeviceAutoConnection;
import jedrzejbronislaw.flowmeasure.view.ViewManager;

public class ConnectionService {
	
	private static final int UART_RATE = 9600;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	private EventManager      eventManager;
	private ConnectionMonitor connectionMonitor;
	private UARTDevice        edDevice;
	private UARTDevice        flowDevice;
	private List<UARTDevice>  devices;
	private ViewManager       viewManager;
	
	
	public ConnectionService() {
		Components.getComponentsLoader().addLoadMethod(() -> {
			eventManager      = Components.getEventManager();
			connectionMonitor = Components.getConnectionMonitor();
			edDevice          = Components.getEdDevice();
			flowDevice        = Components.getFlowDevice();
			devices           = Components.getDevices();
			viewManager       = Components.getViewManager();
		});
	}
	
	
	public ConnectionAttempt createConnectionAttempt(UARTDevice device, UARTParams params) {
		ConnectionAttempt attempt = new ConnectionAttempt(device, params);
		
		attempt.setSuccess(() -> {
			eventManager.submitEvent(EventType.CONNECTION_SUCCESSFUL);
			connectionMonitor.start();
		});
		
		attempt.setFail(reason ->
			eventManager.submitEvent(EventType.CONNECTION_FAILED)
		);
		
		return attempt;
	}

	public AutoConnection createAutoConnection(UARTDevice device) {
		AutoConnection autoConn = new AutoConnection(device, UART.getPortList(), UART_RATE);
		
		autoConn.setIfFail(() -> {
			log.info("¯aden port nie pasuje");
			eventManager.submitEvent(EventType.CONNECTION_FAILED);
		});
		
		autoConn.setIfSuccess(port -> {
			log.info("Uda³o po³¹czyæ siê z portem: {}", port);
			eventManager.submitEvent(EventType.CONNECTION_SUCCESSFUL);
			connectionMonitor.start();
		});
		
		return autoConn;
	}

	public MultiDeviceAutoConnection createMultiDeviceAutoConnection() {
		MultiDeviceAutoConnection autoConn = new MultiDeviceAutoConnection(devices, UART.getPortList(), UART_RATE);
		
		autoConn.setIfFail(() -> {
			log.info("Nie znaleziono ¿adnego urz¹dzenia pod ¿adnym portem");
			eventManager.submitEvent(EventType.CONNECTION_FAILED);
		});
		
		autoConn.setIfSuccess((device, port) -> {
			log.info("Uda³o po³¹czyæ siê z urz¹dzeniem {} na porcie: {}", device.getName(), port);
			eventManager.submitEvent(EventType.CONNECTION_SUCCESSFUL);
			connectionMonitor.start();
			
			if (device == flowDevice) viewManager.setDeviceView(DeviceType.FlowDevice);
			if (device ==   edDevice) viewManager.setDeviceView(DeviceType.  EDDevice);
			
			if (device == flowDevice) Components.getDeviceManager().setDevice(jedrzejbronislaw.flowmeasure.devices2.DeviceType.FlowDevice, device);
			if (device ==   edDevice) Components.getDeviceManager().setDevice(jedrzejbronislaw.flowmeasure.devices2.DeviceType.  EDDevice, device);
		});
		
		return autoConn;
	}
}
