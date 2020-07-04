package jedrzejbronislaw.flowmeasure.tools;

public class NumberTools {
	
	public static String floatToString(float value, String decimalSeparator) {
		String stringFloat = Float.toString(value);
		return stringFloat.replaceAll("[\\.,]", decimalSeparator);
	}
}
