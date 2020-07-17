package jedrzejbronislaw.flowmeasure.tools.settings.poperties;

public class BoolProperty extends Property {

	private boolean value;
	
	public BoolProperty(String defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return Boolean.toString(value);
	}

	@Override
	boolean set(String value) {
		this.value = Boolean.parseBoolean(value);
		return true;
	}
	
	public boolean get() {
		return value;
	}
}
