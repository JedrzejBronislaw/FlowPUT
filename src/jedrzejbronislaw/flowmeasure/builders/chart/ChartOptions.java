package jedrzejbronislaw.flowmeasure.builders.chart;

import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController.ValueUnit;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChartOptions {
	
	@Builder.Default private boolean lastSecOption = false;
	@Builder.Default private ValueUnit unit = ValueUnit.Pulses;
}
