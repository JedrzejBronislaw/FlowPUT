package jedrzejbronislaw.flowmeasure.builders.chart;

import javafx.scene.chart.LineChart;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import lombok.Getter;

public class ChartLpSDataUpdater extends ChartDataUpdater {

	@Getter private final String axisLabel = "flow [" + FlowConverter.FLOW_UNIT + "]";

	private FlowConverters flowconverters;
	private FlowMeasurement measurement, prevMeasurement;
	

	public ChartLpSDataUpdater(LineChart<Number, Number> chart, ChartPointSetter chartPointSetter, FlowConverters flowconverters) {
		super(chart, chartPointSetter);
		this.flowconverters = flowconverters;
	}
	
	@Override
	protected Float measurementCalculation(int measurementNumber) {
		if(measurementNumber == 0) return null;
		
		prevMeasurement = getMeasurement(measurementNumber-1);
		measurement = getMeasurement(measurementNumber);
		
		return timeSec(measurementNumber);
	}

	@Override
	protected float getFlowmeterValue(int flowmeter) {
		float interval = timeSec(prevMeasurement, measurement);
		int pulses = measurement.get(flowmeter);
		
		return flowconverter(flowmeter).pulsesToLitrePerSec(pulses, interval);
	}
	
	private FlowConverter flowconverter(int flowmeter) {
		return flowconverters.get(flowmeter);
	}
}
