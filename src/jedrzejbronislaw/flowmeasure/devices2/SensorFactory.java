package jedrzejbronislaw.flowmeasure.devices2;

import jedrzejbronislaw.flowmeasure.devices2.sensors.Ammeter;
import jedrzejbronislaw.flowmeasure.devices2.sensors.ECSensor;
import jedrzejbronislaw.flowmeasure.devices2.sensors.FlowSensor;
import jedrzejbronislaw.flowmeasure.devices2.sensors.PHSensor;
import jedrzejbronislaw.flowmeasure.devices2.sensors.Sensor;

public class SensorFactory {

	public static Sensor createSensor(SensorType type) {
		switch (type) {
			case FlowMeter: return new FlowSensor();
			case PHMeter:   return new PHSensor();
			case ECSensor:  return new ECSensor();
			case Ammeter:   return new Ammeter();

			default:        return null;
		}
	}
}
