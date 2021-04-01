package jedrzejbronislaw.flowmeasure.devices2.sensors;

import jedrzejbronislaw.flowmeasure.devices2.converters.AmpereConverter;
import lombok.Getter;

public class Ammeter extends Sensor {

	@Getter private String typeName = "Anmeter";

	public Ammeter() {
		super(new AmpereConverter());
	}
}
