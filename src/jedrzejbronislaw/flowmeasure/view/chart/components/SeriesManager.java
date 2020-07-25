package jedrzejbronislaw.flowmeasure.view.chart.components;

import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeriesManager {

	private static final String FLOW_SERIES_NAME_PREFIX = "Flow ";
	
	@NonNull private final LineChart<Number, Number> chart;
	         private List<Series<Number, Number>> seriesList;
	
	public void setChartPoint(int flowmeterNumber, Data<Number, Number> chartPoint) {
		seriesList.get(flowmeterNumber).getData().add(chartPoint);
	}

	public void prepareSeries(int seriesNumber) {
		List<Series<Number, Number>> seriesList = new LinkedList<>();
		ObservableList<Series<Number, Number>> oldSeries = chart.getData();
		
		for(int i=0; i<seriesNumber; i++) {
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

	public void updateSeries() {
		chart.getData().forEach(oldSeries -> {
			if (seriesList.contains(oldSeries))
				seriesList.remove(oldSeries);
		});
		
		seriesList.forEach(newSeries -> {
			if (!chart.getData().contains(newSeries))
				 chart.getData().add(newSeries);
		});
	}

	private String flowSeriesName(int i) {
		return FLOW_SERIES_NAME_PREFIX + (i+1);
	}
}
