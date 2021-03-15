package jedrzejbronislaw.flowmeasure.view.chartED;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.SnapshotSaver;
import jedrzejbronislaw.flowmeasure.tools.loop.Refresher;
import jedrzejbronislaw.flowmeasure.view.chart.components.ChartOptions;
import jedrzejbronislaw.flowmeasure.view.chart.components.ValueUnit;
import lombok.Getter;
import lombok.Setter;

public class ChartEDPaneController implements Initializable {

	private static final int REFRESHING_TIME = 1000;
	
	@FXML private VBox chartPane;
	@FXML private Button refreshButton, saveButton;
	@FXML private CheckBox lastSecsBox, liveBox;

	@Getter private LineChart<Number, Number> chartPH = newChart();
	@Getter private LineChart<Number, Number> chartEC = newChart();
	@Getter private LineChart<Number, Number> chartAM = newChart();
	@Setter private Consumer<ChartOptions> refreshButtonAction;
	
	private Refresher liveChartRefresher = new Refresher(REFRESHING_TIME, this::refresh);

	        
	public LineChart<Number, Number> newChart() {
		return new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		chartPane.getChildren().addAll(chartPH, chartEC, chartAM);
		
		refreshButton.disableProperty().bind(liveBox.selectedProperty());
		
		refreshButton.setOnAction(this::refresh);
		
		saveButton.setOnAction(e -> SnapshotSaver.withFileChooser(chartPane));
		liveBox.setOnAction(e -> {
			if(liveBox.isSelected())
				liveChartRefresher.on(); else
				liveChartRefresher.off();
		});
	}

	private void refresh(ActionEvent x) {refresh();}
	private void refresh() {
		Injection.run(refreshButtonAction, getChartOptions());
	}

	private ChartOptions getChartOptions() {
		return ChartOptions.builder()
				.lastSecOption(isLastSeconds())
				.unit(ValueUnit.PULSES)
				.build();
	}
	
	private boolean isLastSeconds() {
		return lastSecsBox.isSelected();
	}
}
