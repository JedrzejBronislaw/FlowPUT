package jedrzejbronislaw.flowmeasure.tools.settings;

import java.util.Optional;

import jedrzejbronislaw.flowmeasure.tools.settings.poperties.BoolProperty;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.FloatProperty;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.IntProperty;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.Property;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;

public interface PropertyAccess {

	void set(PropertyDesc propertyDesc, String value);
	Property get(PropertyDesc propertyDesc);

	
	default String getPropertyValue(PropertyDesc name) {
		Property property = get(name);
		
		return property==null ? null : property.toString();
	}

	default Optional<Integer> getPropertyIntValue(PropertyDesc name) {
		IntProperty property;
		
		try {
			property = (IntProperty) get(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}

	default Optional<Float> getPropertyFloatValue(PropertyDesc name) {
		FloatProperty property;
		
		try {
			property = (FloatProperty) get(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}

	default Optional<Boolean> getPropertyBoolValue(PropertyDesc name) {
		BoolProperty property;
		
		try {
			property = (BoolProperty) get(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}
	
	
	
	default String getString(PropertyDesc name) {
		return getPropertyValue(name);
	}
	
	default boolean getBool(PropertyDesc name) {
		return getPropertyBoolValue(name).get();
	}

	default int getInt(PropertyDesc name) {
		return getPropertyIntValue(name).get();
	}

	default float getFloat(PropertyDesc name) {
		return getPropertyFloatValue(name).get();
	}
	
	

	default void setProperty(PropertyDesc name, String value) {
		set(name, value);
	}
	
	default void setProperty(PropertyDesc name, int value) {
		set(name, Integer.toString(value));
	}

	default void setProperty(PropertyDesc name, float value) {
		set(name, Float.toString(value));
	}

	default void setProperty(PropertyDesc name, boolean value) {
		set(name, Boolean.toString(value));
	}
}
