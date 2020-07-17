package jedrzejbronislaw.flowmeasure.tools.settings.poperties;

public class IntProperty extends Property {

	private int value;

	public IntProperty(String defaultValue) {
		super(defaultValue);
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	boolean set(String value) {
		
		try {
			this.value = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	public int get() {
		return value;
	}
}
