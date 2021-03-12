package jedrzejbronislaw.flowmeasure.view.chart.components;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChartOptions {
	
	@Builder.Default private boolean lastSecOption = false;
	@Builder.Default private ValueUnit unit = ValueUnit.PULSES;
}
