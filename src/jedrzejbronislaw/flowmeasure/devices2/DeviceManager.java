package jedrzejbronislaw.flowmeasure.devices2;

import java.util.HashMap;
import java.util.Map;

import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import lombok.Getter;

public class DeviceManager {

	private Map<DeviceType, Device> devices = new HashMap<>();
	
	        private UARTDevice uartInterface;
	@Getter private Device currentDevice;
	
	
	public DeviceManager() {
		createDevices();
	}


	private void createDevices() {
		for (DeviceType type : DeviceType.values()) {
			Device device = DeviceFactory.createDevice(type);
			devices.put(type, device);
		}
	}
	
	public void setDevice(DeviceType deviceType, UARTDevice uartInterface) {
		unsetDevice();
		Device device = devices.get(deviceType);
		
		uartInterface.setDataReceiver(device::receiveData);
		
		currentDevice = device;
		this.uartInterface = uartInterface;
	}
	
	public void unsetDevice() {
		if (uartInterface != null) uartInterface.setDataReceiver(null);
		currentDevice = null;
		uartInterface = null;
	}
	
	public Device getDevice(DeviceType deviceType) {
		return devices.get(deviceType);
	}
}
