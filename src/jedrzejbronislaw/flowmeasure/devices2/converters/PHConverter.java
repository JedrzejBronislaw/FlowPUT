package jedrzejbronislaw.flowmeasure.devices2.converters;

public class PHConverter extends ValueConverter {

	private static final float PH_7_VOLTAGE = 3;


	@Override
	public float convert(String data) {
		float value = Integer.parseInt(data);
		value /= 10;
		return valueToPH(value);
	}
	

	public static float valueToPH(float value) {
		return voltageToPH(valueToVoltage(value));
	}
	
	public static float voltageToPH(float voltage) {
		return 7 + ((PH_7_VOLTAGE - voltage) / 0.18f);
	}
}
