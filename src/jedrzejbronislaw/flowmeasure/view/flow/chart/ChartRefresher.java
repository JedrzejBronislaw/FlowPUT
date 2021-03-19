package jedrzejbronislaw.flowmeasure.view.flow.chart;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.ItemSelector;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartDataUpdater;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartFlowDataUpdater;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartOptions;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartPulseDataUpdater;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartRange;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.SeriesManager;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartRange.Range;
import lombok.NonNull;

public class ChartRefresher {
	
	private static final int DATA_SIZE_LIMIT = 1000;
	private static final String AXIS_LABEL_TIME = "time [s]";
	private static final int TICK_UNIT_PX = 100;

	@NonNull private final LineChart<Number, Number> chart;
	
	private ChartOptions options = ChartOptions.builder().build();
	private ProcessRepository process;
	
	private List<FlowMeasurement> data;
	private NumberAxis xAxis;

	private ChartPulseDataUpdater pulseUpdater;
	private ChartFlowDataUpdater  flowUpdater;
	
	private SeriesManager seriesManager;
	private ChartRange chartRange = new ChartRange();
	private Range range;
	
	private ItemSelector<FlowMeasurement> itemSelector = new ItemSelector<>();
	
	
	public ChartRefresher(FlowConverters flowConverters, LineChart<Number, Number> chart, Settings settings) {
		this.chart = chart;
		
		seriesManager = new SeriesManager(chart);
		seriesManager.setSeriesNameSupplier(number -> settings.getString(new FlowmeterNameProperty(number)));
		seriesManager.refreshSeriesNames();
		
		pulseUpdater = new ChartPulseDataUpdater(chart, seriesManager::setChartPoint);
		flowUpdater  = new ChartFlowDataUpdater (chart, seriesManager::setChartPoint, flowConverters);

		xAxis = (NumberAxis) chart.getXAxis();
		
		setChartStaticProperties();
		setXAxisStaticProperties();
		
		settings.addChangeListener(seriesManager::refreshSeriesNames);
	}
	
	public void refresh(ChartOptions options, ProcessRepository process) {
		if (process == null) {clear(); return;}
		
		this.options = options;
		this.process = process;
		data         = process.getAllMeasurement();
		if(data.size() == 0) return;
		
		reduceDataIfNecessary();
		range = chartRange.get(data, options);
		
		Platform.runLater(() -> {
			seriesManager.prepareSeries(process.getNumOfFlowmeters());
			
			updateXAxis();
			updateValues();
			
			seriesManager.updateSeries();
		});
	}

	private void clear() {
		Platform.runLater(() -> {
			chart.getData().clear();
			xAxis.setLowerBound(0);
			xAxis.setUpperBound(100);
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
		xAxis.setMinorTickCount(4);
	}

	private void updateXAxis() {
		float beginTimeSec = timeSec(data.get(range.getFirst()));
		float endTimeSec   = timeSec(data.get(range.getLast()));

		xAxis.setLowerBound(Math.ceil(beginTimeSec));
		xAxis.setUpperBound(Math.ceil(endTimeSec));
		
		xAxis.setTickUnit(tickUnit(beginTimeSec, endTimeSec));
	}

	private int tickUnit(float beginValue, float endValue) {
		double axisWidth = xAxis.getWidth();
		float size = endValue - beginValue + 1;
		
		return (int)(TICK_UNIT_PX * size / axisWidth);
	}
	
	private void reduceDataIfNecessary() {
		if(!options.isLastSecOption() && data.size() > DATA_SIZE_LIMIT)
			data = itemSelector.select(data, DATA_SIZE_LIMIT);
	}

	private void updateValues() {
		chartUpdater().update(range.getFirst(), range.getLast(), process, data);
	}

	private ChartDataUpdater chartUpdater() {
		switch (options.getUnit()) {
			case PULSES:    return pulseUpdater;
			case FLOW_UNIT: return flowUpdater;
			
			default: return pulseUpdater;
		}
	}

	public static float timeSec(LocalDateTime startTime, FlowMeasurement measurement) {
		return ChronoUnit.MILLIS.between(startTime, measurement.getTime()) / 1000f;
	}

	private float timeSec(FlowMeasurement measurement) {
		LocalDateTime startTime = process.getMetadata().getStartTime();
		return timeSec(startTime, measurement);
	}
}
