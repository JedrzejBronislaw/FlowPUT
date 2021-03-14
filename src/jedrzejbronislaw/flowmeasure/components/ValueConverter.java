package jedrzejbronislaw.flowmeasure.components;

public class ValueConverter {

	private static final float PH_7_VOLTAGE = 3;
	

	public static float valueToPH(float value) {
		return voltageToPH(valueToVoltage(value));
	}
	
	public static float valueToAmpere(float value) {
		return voltageToAmpere(valueToVoltage(value));
	}
	
	public static float voltageToPH(float voltage) {
		return 7 + ((PH_7_VOLTAGE - voltage) / 0.18f);
	}
	
	public static float voltageToAmpere(float voltage) {
		return (voltage - 2.5f) * 10;
	}

	public static float valueToVoltage(float value) {
		return value * 5 / 1023;
	}
}
