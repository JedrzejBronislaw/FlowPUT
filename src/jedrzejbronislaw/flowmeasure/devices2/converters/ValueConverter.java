package jedrzejbronislaw.flowmeasure.devices2.converters;

public abstract class ValueConverter {

	public abstract float convert(String data);
	
	public static float valueToVoltage(float value) {
		return value * 5 / 1023;
	}
}
