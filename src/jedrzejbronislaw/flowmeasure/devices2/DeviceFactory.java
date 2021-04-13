package jedrzejbronislaw.flowmeasure.devices2;

import java.util.ArrayList;
import java.util.List;

import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.DeviceDescription;
import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.DeviceDescriptions;
import jedrzejbronislaw.flowmeasure.devices2.sensors.Sensor;

public class DeviceFactory {

	public static Device createDevice(DeviceType type) {
		DeviceDescription desc = DeviceDescriptions.get(type);
		List<SensorType> sensorTypes = desc.getSensors();
		List<Sensor> sensors = new ArrayList<>();
		
		for (int i=0; i<sensorTypes.size(); i++) {
			Sensor sensor = SensorFactory.createSensor(sensorTypes.get(i), i);
			sensors.add(sensor);
		}
		
		System.out.println("Create device: " + type);
		
		return new Device(desc.getName(), sensors);
	}
}
