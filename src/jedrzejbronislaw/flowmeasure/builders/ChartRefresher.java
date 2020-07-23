package jedrzejbronislaw.flowmeasure.builders;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import jedrzejbronislaw.flowmeasure.FlowConverters;
import jedrzejbronislaw.flowmeasure.builders.chart.ChartDataUpdater;
import jedrzejbronislaw.flowmeasure.builders.chart.ChartLpSDataUpdater;
import jedrzejbronislaw.flowmeasure.builders.chart.ChartOptions;
import jedrzejbronislaw.flowmeasure.builders.chart.ChartPulseDataUpdater;
import jedrzejbronislaw.flowmeasure.builders.chart.ChartRange;
import jedrzejbronislaw.flowmeasure.builders.chart.ChartRange.Range;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.NonNull;

public class ChartRefresher {
	
	private static final String FLOW_SERIES_NAME_PREFIX = "Flow ";
	private static final String AXIS_LABEL_TIME = "time [s]";

	@NonNull private final LineChart<Number, Number> chart;
	
	private ChartOptions options = ChartOptions.builder().build();
	private ProcessRepository process;
	
	private List<FlowMeasurement> data;
	private int numOfFlowMeters;
	private NumberAxis xAxis;
	private List<Series<Number, Number>> seriesList;
	
	

	private ChartPulseDataUpdater pulseUpdater;
	private ChartLpSDataUpdater lpsUpdater;
	

	private ChartRange chartRange = new ChartRange();
	private Range range;
	
	public ChartRefresher(FlowConverters flowConverters, LineChart<Number, Number> chart) {
		this.chart = chart;
		
		pulseUpdater = new ChartPulseDataUpdater(chart, this::setChartPoint);
		lpsUpdater   = new ChartLpSDataUpdater  (chart, this::setChartPoint, flowConverters);

		xAxis = (NumberAxis) chart.getXAxis();
		
		setChartStaticProperties();
		setXAxisStaticProperties();
	}
	
	public void refresh(ChartOptions options, ProcessRepository process) {
		if (process == null) return;
		
		this.options = options;
		this.process = process;
		data            = process.getAllMeasurement();
		numOfFlowMeters = process.getNumOfFlowmeters();
		if(data.size() == 0) return;
		
		range = chartRange.get(data, options);
		
		Platform.runLater(() -> {
			prepareSeries();
			
			updateXAxis();
			updateValues();
			
			addSeries();
		});
	}

	private void setChartStaticProperties() {
		chart.setCreateSymbols(false);
		chart.setAnimated(false);
	}
	
	private void setXAxisStaticProperties() {
		xAxis.setLabel(AXIS_LABEL_TIME);
		xAxis.setForceZeroInRange(false);
		xAxis.setAutoRanging(false);
		xAxis.setTickUnit(1);
		xAxis.setMinorTickCount(5);
	}

	private void updateXAxis() {
		float beginTimeSec = timeSec(data.get(range.getFirst()));
		float endTimeSec   = timeSec(data.get(range.getLast()));

		xAxis.setLowerBound(Math.ceil(beginTimeSec));
		xAxis.setUpperBound(Math.ceil(endTimeSec));
	}

	private void updateValues() {
		chartUpdater().update(range.getFirst(), range.getLast(), process);
	}

	private ChartDataUpdater chartUpdater() {
		switch (options.getUnit()) {
			case Pulses:      return pulseUpdater;
			case LitrePerSec: return lpsUpdater;
			
			default: return pulseUpdater;
		}
	}

	private void setChartPoint(int flowmeterNumber, Data<Number, Number> chartPoint) {
		seriesList.get(flowmeterNumber).getData().add(chartPoint);
	}

	private void prepareSeries() {
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

	private void addSeries() {
		chart.getData().forEach(oldSeries -> {
			if (seriesList.contains(oldSeries))
				seriesList.remove(oldSeries);
		});
		
		seriesList.forEach(newSeries -> {
			if (!chart.getData().contains(newSeries))
				 chart.getData().add(newSeries);
		});
	}

	public static float timeSec(LocalDateTime startTime, FlowMeasurement measurement) {
		return ChronoUnit.MILLIS.between(startTime, measurement.getTime()) / 1000f;
	}

	private float timeSec(FlowMeasurement measurement) {
		LocalDateTime startTime = process.getMetadata().getStartTime();
		return timeSec(startTime, measurement);
	}

	private String flowSeriesName(int i) {
		return FLOW_SERIES_NAME_PREFIX + (i+1);
	}
}
