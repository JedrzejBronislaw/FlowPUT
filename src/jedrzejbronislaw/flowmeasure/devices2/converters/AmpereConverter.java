package jedrzejbronislaw.flowmeasure.devices2.converters;

public class AmpereConverter extends ValueConverter {

	@Override
	public float convert(String data) {
		float value = Integer.parseInt(data);
		value /= 10;
		return valueToAmpere(value);
	}
	
	
	public static float valueToAmpere(float value) {
		return voltageToAmpere(valueToVoltage(value));
	}
	
	public static float voltageToAmpere(float voltage) {
		return (voltage - 2.5f) * 10;
	}
}
