package jedrzejbronislaw.flowmeasure.devices2.sensors;

import jedrzejbronislaw.flowmeasure.devices2.converters.NoConverter;
import lombok.Getter;

public class FlowSensor extends Sensor {

	@Getter private String typeName = "FlowSensor";
	
	public FlowSensor() {
		super(new NoConverter());
	}
}
