package jedrzejbronislaw.flowmeasure.devices2.sensors;

import jedrzejbronislaw.flowmeasure.devices2.converters.PHConverter;
import lombok.Getter;

public class PHSensor extends Sensor {

	@Getter private String typeName = "PHSensor";

	public PHSensor() {
		super(new PHConverter());
	}
}
