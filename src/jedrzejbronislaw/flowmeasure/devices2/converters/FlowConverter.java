package jedrzejbronislaw.flowmeasure.devices2.converters;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class FlowConverter extends ValueConverter {

	@Setter private Supplier<Float> ratioSupplier;

	
	@Override
	public float convert(String data) {
		int value = Integer.parseInt(data);
		
		return pulsesToLitre(value);
	}
	
	private float pulsesToLitre(int pulses) {
		return pulses/factor();
	}
	
	private float factor() {
		return Injection.get(ratioSupplier, 1f);
	}
}
