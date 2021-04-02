package jedrzejbronislaw.flowmeasure.devices2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.DeviceDescription;
import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.DeviceDescriptions;
import jedrzejbronislaw.flowmeasure.devices2.sensors.Sensor;

public class DeviceFactory {

	public static Device createDevice(DeviceType type) {
		DeviceDescription desc = DeviceDescriptions.get(type);
		List<Sensor> sensors = new ArrayList<>();
		
		sensors = desc.getSensors().stream()
				.map(SensorFactory::createSensor)
				.collect(Collectors.toList());
		
		System.out.println("Create device: " + type);
		
		return new Device(desc.getName(), sensors);
	}
}
