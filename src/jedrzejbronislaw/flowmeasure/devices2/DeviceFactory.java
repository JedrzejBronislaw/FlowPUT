package jedrzejbronislaw.flowmeasure.devices2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.DeviceDescription;
import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.EDDeviceDesc;
import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.FlowmeterDesc;
import jedrzejbronislaw.flowmeasure.devices2.sensors.Sensor;

public class DeviceFactory {

	public static DeviceDescription getDescription(DeviceType type) {
		switch (type) {
			case FlowDevice: return new FlowmeterDesc();
			case EDDevice:   return new EDDeviceDesc();
			default:         return null;
		}
	}
	
	
	public static Device createDevice(DeviceType type) {
		DeviceDescription desc = getDescription(type);
		List<Sensor> sensors = new ArrayList<>();
		
		sensors = desc.getSensors().stream()
				.map(SensorFactory::createSensor)
				.collect(Collectors.toList());
		
		System.out.println("Create device: " + type);
		
		return new Device(desc.getName(), sensors);
	}
}
