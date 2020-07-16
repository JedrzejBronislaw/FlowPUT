package jedrzejbronislaw.flowmeasure.settings;

import java.util.HashMap;
import java.util.Map;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class Properties implements PropertyAccess {

	private Map<String, Property> properties = new HashMap<>();
	
	@Setter
	private Runnable changeAction;

	
	public void add(PropertyName name, Property property) {
		properties.put(name.toString(), property);
	}


	@Override
	public void set(PropertyName name, String value) {
		Property property = properties.get(name.toString());
		
		if(property != null && value != null && property.set(value))
			Injection.run(changeAction);
	}

	@Override
	public Property get(PropertyName name) {
		return properties.get(name.toString());
	}
}
