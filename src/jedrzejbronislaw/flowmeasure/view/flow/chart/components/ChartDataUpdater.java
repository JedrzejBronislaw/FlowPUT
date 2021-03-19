package jedrzejbronislaw.flowmeasure.view.flow.chart.components;

import java.time.LocalDateTime;
import java.util.List;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.view.flow.chart.ChartRefresher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ChartDataUpdater {
	
	public static interface ChartPointSetter {
		void setPoint(int flowmeterNumber, Data<Number, Number> chartPoint);
	}

	protected abstract String getAxisLabel();
	protected abstract Float measurementCalculation(int measurementNumber);
	protected abstract float getFlowmeterValue(int flowmeter);
	
	@NonNull private final LineChart<Number, Number> chart;
	@NonNull private final ChartPointSetter chartPointSetter;
	private List<FlowMeasurement> data;
	private ProcessRepository process;
	
	private List<Integer> filter;
	
	
	public void update(int firstIndex, int lastIndex, ProcessRepository process, List<FlowMeasurement> data) {
		Float time;

		this.process = process;
		this.data = data;
		
		chart.getYAxis().setLabel(getAxisLabel());
		
		for (int i=firstIndex; i<=lastIndex; i++) {
			
			time = measurementCalculation(i);
			if (time == null) continue;
		
			
			if (filter != null)
				for (Integer flowmeter : filter)
					setPoint(time, flowmeter);
			else
				for (int flowmeter=0; flowmeter<process.getNumOfFlowmeters(); flowmeter++)
					setPoint(time, flowmeter);
		}
	}
	private void setPoint(Float time, int flowmeter) {
		Data<Number, Number> chartPoint;
		float value;
		
		value = getFlowmeterValue(flowmeter);
		chartPoint = new Data<Number, Number>(time, value);
		chartPointSetter.setPoint(flowmeter, chartPoint);
	}
	
	protected FlowMeasurement getMeasurement(int number) {
		return data.get(number);
	}
	
	protected float timeSec(int measurementNumber) {
		LocalDateTime startTime = process.getMetadata().getStartTime();
		return ChartRefresher.timeSec(startTime, getMeasurement(measurementNumber));
	}
	
	protected float timeSec(FlowMeasurement startMeasurement, FlowMeasurement measurement) {
		return ChartRefresher.timeSec(startMeasurement.getTime(), measurement);
	}
	
	public ChartDataUpdater setFilter(List<Integer> filter) {
		this.filter = filter;
		return this;
	}
}
