package jedrzejbronislaw.flowmeasure.tools.settings.poperties;

import java.util.HashMap;
import java.util.Map;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.settings.PropertyAccess;
import lombok.Setter;

public class Properties implements PropertyAccess {

	private Map<String, Property> properties = new HashMap<>();
	
	@Setter private Runnable changeAction;


	public void add(PropertyDesc[] values) {
		for (PropertyDesc property : values) add(property);
	}
	public void add(PropertyDesc propertyDesc) {
		properties.put(propertyDesc.getName(), createProperty(propertyDesc));
	}
	
	private Property createProperty(PropertyDesc propery) {
		
		switch (propery.getType()) {
			case STRING: return new StringProperty(propery.getDefaultValue());
			case INT:    return new    IntProperty(propery.getDefaultValue());
			case FLOAT:  return new  FloatProperty(propery.getDefaultValue());
			case BOOL:   return new   BoolProperty(propery.getDefaultValue());
	
			default:     return new StringProperty(propery.getDefaultValue());
		}
	}

	@Override
	public void set(PropertyDesc propertyDesc, String value) {
		Property property = properties.get(propertyDesc.getName());
		
		if(property != null && value != null && property.set(value))
			Injection.run(changeAction);
	}

	@Override
	public Property get(PropertyDesc propertyDesc) {
		return properties.get(propertyDesc.getName());
	}
}
