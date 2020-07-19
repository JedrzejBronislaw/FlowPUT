package jedrzejbronislaw.flowmeasure.settings;

import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RatioProperty implements PropertyDesc {

	private final int number;
	private final String name = "pulseRatio";
	@Getter private final PropertyType type = PropertyType.FLOAT;
	@Getter private final String defaultValue = "450";
	
	@Override
	public String getName() {
		return name + number;
	}
	
	public static RatioProperty[] generate(int amount) {
		if (amount <= 0) return null;
		
		RatioProperty[] properties = new RatioProperty[amount];
		for (int i=0; i<amount; i++)
			properties[i] = new RatioProperty(i);
		
		return properties;
	}
}
