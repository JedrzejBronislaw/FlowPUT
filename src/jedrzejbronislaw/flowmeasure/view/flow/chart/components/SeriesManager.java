package jedrzejbronislaw.flowmeasure.view.flow.chart.components;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.NonNull;
import lombok.Setter;

public class SeriesManager {

	private static final String FLOW_SERIES_NAME_PREFIX = "Flow ";
	
	@NonNull private final LineChart<Number, Number> chart;
	         private List<Series<Number, Number>> seriesList;
	         
	         private final SeriesVisibilityManager seriesVisibilityManager;
	         private boolean isNewSeries = false;
	         
	@Setter private Function<Integer, String> seriesNameSupplier;


	public SeriesManager(LineChart<Number, Number> chart) {
		this.chart = chart;
		seriesVisibilityManager = new SeriesVisibilityManager(chart);
	}
	         
	public void refreshSeriesNames() {
		if (seriesList == null) return;
		
		for (int i=0; i<seriesList.size(); i++)
			seriesList.get(i).setName(flowSeriesName(i));
	}
	
	public void setChartPoint(int flowmeterNumber, Data<Number, Number> chartPoint) {
		Series<Number, Number> series = seriesList.get(flowmeterNumber);

		if (!seriesVisibilityManager.isVisible(series)) return;
		
		series.getData().add(chartPoint);
	}

	public void prepareSeries(int seriesNumber) {
		List<Series<Number, Number>> seriesList = new LinkedList<>();
		ObservableList<Series<Number, Number>> oldSeries = chart.getData();
		isNewSeries = false;
		
		for (int i=0; i<seriesNumber; i++) {
			if (i < oldSeries.size()) {
				//series reuse
				Series<Number, Number> series = oldSeries.get(i);
				series.getData().clear();
				seriesList.add(series);
			} else {
				Series<Number, Number> series = new Series<>();
				series.setName(flowSeriesName(i));
				seriesList.add(series);
				
				isNewSeries = true;
			}
		}
		
		this.seriesList = seriesList;
	}

	public void updateSeries() {
		chart.getData().forEach(oldSeries -> {
			if (seriesList.contains(oldSeries))
				seriesList.remove(oldSeries);
		});
		
		seriesList.forEach(newSeries -> {
			if (!chart.getData().contains(newSeries))
				 chart.getData().add(newSeries);
		});
		
		if (isNewSeries) seriesVisibilityManager.update();
	}

	private String flowSeriesName(int i) {
		String defaultName = FLOW_SERIES_NAME_PREFIX + (i+1);
		return Injection.get(seriesNameSupplier, i, defaultName);
	}
}
