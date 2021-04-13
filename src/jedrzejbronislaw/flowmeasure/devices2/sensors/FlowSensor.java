package jedrzejbronislaw.flowmeasure.devices2.sensors;

import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.devices2.converters.FlowConverter;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import lombok.Getter;

public class FlowSensor extends Sensor {

	@Getter private String typeName = "FlowSensor";
	
	
	public FlowSensor(int flowSensorNumber) {
		super();
		
		FlowConverter flowConverter = new FlowConverter();
		converter = flowConverter;
		
		Components.getComponentsLoader().addLoadMethod(() -> {
			RatioProperty ratioProperty = new RatioProperty(flowSensorNumber);
			flowConverter.setRatioSupplier(() -> Components.getSettings().getFloat(ratioProperty));
		});
	}
}
