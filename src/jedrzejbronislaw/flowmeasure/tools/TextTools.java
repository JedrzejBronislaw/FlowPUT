package jedrzejbronislaw.flowmeasure.tools;

public class TextTools {
	
	public static String firstCharUpper(String text) {
		String firstChar = text.substring(0, 1).toUpperCase();
		String rest      = text.substring(1)   .toLowerCase();
		
		return firstChar + rest;
	}

	public static String removeUnderscores(String text) {
		return text.replace('_', ' ');
	}
}
