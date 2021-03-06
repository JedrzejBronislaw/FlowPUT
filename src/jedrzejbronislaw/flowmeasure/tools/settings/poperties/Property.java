package jedrzejbronislaw.flowmeasure.tools.settings.poperties;

public abstract class Property {

	String defaultValue;
	
	public Property(String defaultValue) {
		this.defaultValue = defaultValue;
		setDefaultValue();
	}

	void setDefaultValue() {
		set(defaultValue);
	}
	
	public abstract String toString();
	abstract boolean set(String value); 
}
