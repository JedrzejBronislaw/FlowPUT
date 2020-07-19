package jedrzejbronislaw.flowmeasure.tools.settings.poperties;

import java.util.stream.Stream;

public interface PropertyDesc {

	PropertyType getType();
	String getName();
	String getDefaultValue();
	
	static PropertyDesc[] sum(PropertyDesc[]... properties) {
		return Stream.of(properties).flatMap(p -> Stream.of(p)).toArray(PropertyDesc[]::new);
	}
}
