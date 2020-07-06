package jedrzejbronislaw.flowmeasure.builders;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController.ValueUnit;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.tools.ItemSelector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartRefresher implements Consumer<LineChart<Number, Number>> {
	
	private static final int LAST_SEC_NUMER = 60;
	private static final String FLOW_SERIES_NAME_PREFIX = "Flow ";
	private static final String AXIS_LABEL_TIME = "time [s]";
	private static final String AXIS_LABEL_PULSES = "flow [pulses]";
	private static final String AXIS_LABEL_L_PER_SEC = "flow [" + FlowConverter.FLOW_UNIT + "]";

	private final Supplier<ProcessRepository> currentProcess;
	private final FlowConverter flowconverter;
	private final ChartPaneController controller;
	
	private List<FlowMeasurement> data;
	private ProcessRepository process;

	private NumberAxis axisX;
	private NumberAxis axisY;
	private List<Series<Number, Number>> seriesList;
	private int numOfFlowMeters;
	private boolean lastSecOption;
	private ValueUnit unit;
	
	private int firstMeasureIndex;
	private int lastMeasureIndex;
	
	@Override
	public void accept(LineChart<Number, Number> chart) {
		Platform.runLater(() -> {
			updateLocalVars();
			if(data.size() == 0) return;
			
			prepareSeries(chart);
			prepareScopeOfData();
			
			
			updateAxisX();
			updateValues();
			
			
			chartSettings(chart);
			addSeries(chart);
		});
	}

	private void chartSettings(LineChart<Number, Number> chart) {
		chart.setCreateSymbols(false);
		chart.setAnimated(false);
	}

	private void prepareScopeOfData() {
		if(!lastSecOption && data.size() > 1000)
			data = new ItemSelector<FlowMeasurement>().select(data, 1000);

		firstMeasureIndex = getFirst();
		lastMeasureIndex  = getLast();
	}

	private void updateLocalVars() {
		process = currentProcess.get();
		
		data            = process.getAllMeasurement();
		numOfFlowMeters = process.getNumOfFlowmeters();

		lastSecOption = controller.isLastSeconds();
		unit          = controller.getValueUnit();
		axisX         = controller.getAxisX();
		axisY         = controller.getAxisY();
	}

	private void updateValues() {
		if (unit == ValueUnit.Pulses)      updateValues_Pulses();
		if (unit == ValueUnit.LitrePerSec) updateValues_LitrePerSec();
	}

	private void updateValues_Pulses() {
		FlowMeasurement measurement;
		Data<Number, Number> chartPoint;
		Series<Number, Number> series;
		float time;

		axisY.setLabel(AXIS_LABEL_PULSES);
		
		for (int i=firstMeasureIndex; i<=lastMeasureIndex; i++) {
			measurement = data.get(i);
			time = timeSec(measurement);
		
			for (int flowmeter=0; flowmeter<numOfFlowMeters; flowmeter++) {
				chartPoint = createChartPoint(time,  measurement.get(flowmeter));
				series = seriesList.get(flowmeter);
				
				series.getData().add(chartPoint);
			}
		}
	}

	private void updateValues_LitrePerSec() {
		FlowMeasurement measurement, prevMeasurement;
		Series<Number, Number> series;
		Data<Number, Number> chartPoint;
		float time;
		float interval;
		int pulses;
		float value;
		
		axisY.setLabel(AXIS_LABEL_L_PER_SEC);

		for (int i=firstMeasureIndex; i<=lastMeasureIndex; i++) {
			if(i == 0) continue;
			prevMeasurement = data.get(i-1);
			measurement = data.get(i);
			time = timeSec(measurement);

			for (int flowmeter=0; flowmeter<numOfFlowMeters; flowmeter++) {
				series = seriesList.get(flowmeter);

				interval = timeSec(prevMeasurement.getTime(), measurement);
				pulses = measurement.get(flowmeter);
				value = flowconverter.pulsesToLitrePerSec(pulses, interval);
				
				chartPoint = createChartPoint(time,  value);
				
				series.getData().add(chartPoint);
			}
		}
	}
	
	private Data<Number, Number> createChartPoint(float time, float value) {
		return new Data<Number, Number>(time, value);
	}

	private void updateAxisX() {
		float beginTimeSec = timeSec(data.get(firstMeasureIndex));
		float endTimeSec   = timeSec(data.get(lastMeasureIndex));
		
		axisX.setLabel(AXIS_LABEL_TIME);
		axisX.setAutoRanging(false);
		axisX.setLowerBound(beginTimeSec);
		axisX.setUpperBound(endTimeSec);
//		double axisWidth = axisX.getWidth();
//		axisX.setTickUnit(axisWidth/50);
//		axisX.setMinorTickLength(5);
//		axisX.setMinorTickCount((int)(axisWidth/10));
		axisX.setTickUnit(1);
		axisX.setMinorTickCount(5);
	}
	
	private int getFirst() {
		if (!lastSecOption) return 0;

		int lastIndex = getLast();
		FlowMeasurement lastMeasure = data.get(lastIndex);
		
		for (int i=lastIndex-1; i>=0; i--)
			if (timeSec(data.get(i).getTime(), lastMeasure) >= LAST_SEC_NUMER)
				return i;
		
		return 0;
	}

	private int getLast() {
		return data.size()-1;
	}

	private void addSeries(LineChart<Number, Number> chart) {
		chart.getData().forEach(oldSeries -> {
			if (seriesList.contains(oldSeries))
				seriesList.remove(oldSeries);
		});
		
		seriesList.forEach(newSeries -> {
			if (!chart.getData().contains(newSeries))
				 chart.getData().add(newSeries);
		});
	}

	private float timeSec(LocalDateTime startTime, FlowMeasurement measurement) {
		return ChronoUnit.MILLIS.between(startTime, measurement.getTime()) / 1000f;
	}

	private float timeSec(FlowMeasurement measurement) {
		LocalDateTime startTime = process.getMetadata().getStartTime();
		return timeSec(startTime, measurement);
	}

	private void prepareSeries(LineChart<Number, Number> chart) {
		List<Series<Number, Number>> seriesList = new LinkedList<>();
		ObservableList<Series<Number, Number>> oldSeries = chart.getData();
		
		for(int i=0; i<numOfFlowMeters; i++) {
			if(i < oldSeries.size()) {
				//series reuse
				Series<Number, Number> series = oldSeries.get(i);
				series.getData().clear();
				seriesList.add(series);
			} else {
				Series<Number, Number> series = new Series<>();
				series.setName(flowSeriesName(i));
				seriesList.add(series);
			}
		}
		
		this.seriesList = seriesList;
	}

	private String flowSeriesName(int i) {
		return FLOW_SERIES_NAME_PREFIX + (i+1);
	}
}
