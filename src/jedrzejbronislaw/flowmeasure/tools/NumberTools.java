package jedrzejbronislaw.flowmeasure.tools;

import java.text.DecimalFormat;

public class NumberTools {
	
	public static String floatToString(float value, String decimalSeparator) {
		String stringFloat = Float.toString(value);
		return changeDecimalSeparator(stringFloat, decimalSeparator);
	}
	
	public static String floatToString(float value, String decimalSeparator, DecimalFormat decimalFormat) {
		String stringFloat = decimalFormat.format(value);
		return changeDecimalSeparator(stringFloat, decimalSeparator);
	}
	
	private static String changeDecimalSeparator(String value, String decimalSeparator) {
		return value.replaceAll("[\\.,]", decimalSeparator);
	}
}
