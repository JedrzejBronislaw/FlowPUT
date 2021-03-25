package jedrzejbronislaw.flowmeasure.view.flow.chart.components;

import javafx.scene.chart.LineChart;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverter;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowUnit;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import lombok.Setter;

public class ChartFlowDataUpdater extends ChartDataUpdater {

	private final static String AXIS_LABEL = "flow";

	private FlowConverters flowconverters;
	private FlowMeasurement measurement, prevMeasurement;

	@Setter private FlowUnit flowUnit = FlowUnit.LITER_PER_HOUR;
	
	
	@Override
	protected String getAxisLabel() {
		return AXIS_LABEL + " [" + flowUnit + "]";
	}

	public ChartFlowDataUpdater(LineChart<Number, Number> chart, ChartPointSetter chartPointSetter, FlowConverters flowconverters) {
		super(chart, chartPointSetter);
		this.flowconverters = flowconverters;
	}
	
	@Override
	protected Float measurementCalculation(int measurementNumber) {
		if (measurementNumber == 0) return null;
		
		prevMeasurement = getMeasurement(measurementNumber-1);
		measurement = getMeasurement(measurementNumber);
		
		return timeSec(measurementNumber);
	}

	@Override
	protected float getFlowmeterValue(int flowmeter) {
		float interval = timeSec(prevMeasurement, measurement);
		int pulses = measurement.get(flowmeter);
		
		return flowconverter(flowmeter).toFlow(pulses, interval, flowUnit);
	}
	
	private FlowConverter flowconverter(int flowmeter) {
		return flowconverters.get(flowmeter);
	}

}
