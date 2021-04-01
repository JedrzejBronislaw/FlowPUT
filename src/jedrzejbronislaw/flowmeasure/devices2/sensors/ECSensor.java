package jedrzejbronislaw.flowmeasure.devices2.sensors;

import jedrzejbronislaw.flowmeasure.devices2.converters.NoConverter;
import lombok.Getter;

public class ECSensor extends Sensor {

	@Getter private String typeName = "ECSensor";

	public ECSensor() {
		super(new NoConverter());
	}
}
