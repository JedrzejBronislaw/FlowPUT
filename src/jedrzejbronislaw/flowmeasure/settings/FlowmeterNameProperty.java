package jedrzejbronislaw.flowmeasure.settings;

import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlowmeterNameProperty implements PropertyDesc {

	        private final int number;
	        private final String name = "flowmeterName";
	@Getter private final PropertyType type = PropertyType.STRING;
	
	@Override
	public String getName() {
		return name + number;
	}
	
	@Override
	public String getDefaultValue() {
		return "Flow " + (number+1);
	}
	
	public static FlowmeterNameProperty[] generate(int amount) {
		if (amount <= 0) return null;
		
		FlowmeterNameProperty[] properties = new FlowmeterNameProperty[amount];
		for (int i=0; i<amount; i++)
			properties[i] = new FlowmeterNameProperty(i);
		
		return properties;
	}
}
