package jedrzejbronislaw.flowmeasure.settings;

import java.util.Optional;

public interface PropertyAccess {

	void set(PropertyName name, String value);
	Property get(PropertyName name);


	
	default String getPropertyValue(PropertyName name) {
		Property property = get(name);
		
		return property==null ? null : property.toString();
	}

	default Optional<Integer> getPropertyIntValue(PropertyName name) {
		IntProperty property;
		
		try {
			property = (IntProperty) get(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}

	default Optional<Float> getPropertyFloatValue(PropertyName name) {
		FloatProperty property;
		
		try {
			property = (FloatProperty) get(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}

	default Optional<Boolean> getPropertyBoolValue(PropertyName name) {
		BoolProperty property;
		
		try {
			property = (BoolProperty) get(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}
	
	
	
	default String getString(PropertyName name) {
		return getPropertyValue(name);
	}
	
	default boolean getBool(PropertyName name) {
		return getPropertyBoolValue(name).get();
	}

	default int getInt(PropertyName name) {
		return getPropertyIntValue(name).get();
	}

	default float getFloat(PropertyName name) {
		return getPropertyFloatValue(name).get();
	}
	
	

	default void setProperty(PropertyName name, String value) {
		set(name, value);
	}
	
	default void setProperty(PropertyName name, int value) {
		set(name, Integer.toString(value));
	}

	default void setProperty(PropertyName name, float value) {
		set(name, Float.toString(value));
	}

	default void setProperty(PropertyName name, boolean value) {
		set(name, Boolean.toString(value));
	}
}
