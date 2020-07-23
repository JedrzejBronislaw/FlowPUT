package jedrzejbronislaw.flowmeasure.builders.chart;

import javafx.scene.chart.LineChart;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import lombok.Getter;

public class ChartPulseDataUpdater extends ChartDataUpdater {

	@Getter private final String axisLabel = "flow [pulses]";

	private FlowMeasurement measurement;
	

	public ChartPulseDataUpdater(LineChart<Number, Number> chart, ChartPointSetter chartPointSetter) {
		super(chart, chartPointSetter);
	}
	
	@Override
	protected Float measurementCalculation(int measurementNumber) {
		measurement = getMeasurement(measurementNumber);
		
		return timeSec(measurementNumber);
	}

	@Override
	protected float getFlowmeterValue(int flowmeter) {
		return measurement.get(flowmeter);
	}
}
