package jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions;

import jedrzejbronislaw.flowmeasure.devices2.SensorType;

public class EDDeviceDesc extends DeviceDescription {

	public EDDeviceDesc() {
		addSensor(SensorType.PHMeter);
		addSensor(SensorType.ECSensor);
		addSensor(SensorType.Ammeter);
	}
}
