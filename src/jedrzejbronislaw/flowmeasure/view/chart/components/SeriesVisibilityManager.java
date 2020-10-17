package jedrzejbronislaw.flowmeasure.view.chart.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sun.javafx.charts.Legend;

import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeriesVisibilityManager {

	@NonNull private final LineChart<Number, Number> chart;

    private Set<Series<Number, Number>> invisibleSeries = new HashSet<>();
    
    public boolean isVisible(Series<Number, Number> series) {
    	return !invisibleSeries.contains(series);
    }
	
	public void update() {
		
		for (Label legendItem : getLegendItems()) {
			legendItem.setCursor(Cursor.HAND);
			legendItem.setOnMouseClicked(e -> legendClick(legendItem));
		}
	}

	private void legendClick(Label label) {
		boolean newVisibility = !label.getGraphic().isVisible();
		
		label.getGraphic().setVisible(newVisibility);
		
		for (Series<Number, Number> series : getSeries(label.getText())) {
			
			series.getNode().setVisible(newVisibility);
			
			if (newVisibility)
				invisibleSeries.remove(series); else
				invisibleSeries.add(series);
		}
	}
	
	private List<Label> getLegendItems() {
		return chart.getChildrenUnmodifiable().stream()
				.filter(node -> node instanceof Legend).map(node -> (Legend)node)
				.flatMap(legend -> legend.getChildren().stream())
				.filter(node -> node instanceof Label) .map(node -> (Label)node)
				.collect(Collectors.toList());
	}
	
	private List<Series<Number, Number>> getSeries(String name) {
		return chart.getData().stream()
				.filter(series -> series.getName().equals(name))
				.collect(Collectors.toList());
	}
}
