package jedrzejbronislaw.flowmeasure.settings;

public class StringProperty extends Property {

	private String value;

	public StringProperty(String defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	boolean set(String value) {
		this.value = value;
		return true;
	}
	
	public String get() {
		return value;
	}
}
