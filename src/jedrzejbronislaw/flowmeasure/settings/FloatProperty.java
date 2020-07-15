package jedrzejbronislaw.flowmeasure.settings;

public class FloatProperty extends Property {

	private float value;

	public FloatProperty(String defaultValue) {
		super(defaultValue);
	}

	@Override
	public String toString() {
		return Float.toString(value);
	}

	@Override
	boolean set(String value) {
		
		try {
			this.value = Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	public float get() {
		return value;
	}
}
