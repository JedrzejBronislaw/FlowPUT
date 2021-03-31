package jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions;

import jedrzejbronislaw.flowmeasure.devices2.SensorType;

public class FlowmeterDesc extends DeviceDescription {

	public FlowmeterDesc() {
		addSensors(SensorType.FlowMeter, 6);
	}
}
