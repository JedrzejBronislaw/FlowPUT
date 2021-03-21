package jedrzejbronislaw.flowmeasure.view;

import java.util.List;

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

public class ConnectionService {
	
	private static final int UART_RATE = 9600;
	
	
	public ConnectionAttempt createConnectionAttempt(UARTDevice device, UARTParams params) {
		ConnectionAttempt attempt = new ConnectionAttempt(device, params);
		
		attempt.setSuccess(() -> {
			eventManager().submitEvent(EventType.CONNECTION_SUCCESSFUL);
			connectionMonitor().start();
		});
		
		attempt.setFail(reason ->
			eventManager().submitEvent(EventType.CONNECTION_FAILED)
		);
		
		return attempt;
	}

	public AutoConnection createAutoConnection(UARTDevice device) {
		AutoConnection autoConn = new AutoConnection(device, UART.getPortList(), UART_RATE);
		
		autoConn.setIfFail(() -> {
			System.out.println("�aden port nie pasuje");
			eventManager().submitEvent(EventType.CONNECTION_FAILED);
		});
		
		autoConn.setIfSuccess(port -> {
			System.out.println("Uda�o po��czy� si� z portem: " + port);
			eventManager().submitEvent(EventType.CONNECTION_SUCCESSFUL);
			connectionMonitor().start();
		});
		
		return autoConn;
	}

	public MultiDeviceAutoConnection createMultiDeviceAutoConnection() {
		MultiDeviceAutoConnection autoConn = new MultiDeviceAutoConnection(devices(), UART.getPortList(), UART_RATE);
		
		autoConn.setIfFail(() -> {
			System.out.println("Nie znaleziono �adnego urz�dzenia pod �adnym portem");
			eventManager().submitEvent(EventType.CONNECTION_FAILED);
		});
		
		autoConn.setIfSuccess((device, port) -> {
			System.out.println("Uda�o po��czy� si� z urz�dzeniem " + device.getName() + " na porcie: " + port);
			eventManager().submitEvent(EventType.CONNECTION_SUCCESSFUL);
			connectionMonitor().start();
			
			if (device == flowDevice()) Components.getViewBuilder().setDeviceView(DeviceType.FlowDevice);
			if (device ==   edDevice()) Components.getViewBuilder().setDeviceView(DeviceType.  EDDevice);
		});
		
		return autoConn;
	}
	
	private EventManager eventManager() {
		return Components.getEventManager();
	}

	private ConnectionMonitor connectionMonitor() {
		return Components.getConnectionMonitor();
	}

	private UARTDevice edDevice() {
		return Components.getEdDevice();
	}

	private UARTDevice flowDevice() {
		return Components.getFlowDevice();
	}

	private List<UARTDevice> devices() {
		return Components.getDevices();
	}
}