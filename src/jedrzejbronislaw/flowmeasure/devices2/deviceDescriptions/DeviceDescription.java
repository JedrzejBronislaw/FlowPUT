package jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jedrzejbronislaw.flowmeasure.devices2.SensorType;
import lombok.Getter;

public abstract class DeviceDescription {

	@Getter protected String name = "Device";
	private List<SensorType> sensors = new ArrayList<>();
	

	protected void addSensor(SensorType type) {
		sensors.add(type);
	}
	
	protected void addSensors(SensorType type, int amount) {
		if (amount < 0) return;
		
		for (int i = 0; i < amount; i++)
			sensors.add(type);
	}

	public List<SensorType> getSensors() {
		return Collections.unmodifiableList(sensors);
	}
}
