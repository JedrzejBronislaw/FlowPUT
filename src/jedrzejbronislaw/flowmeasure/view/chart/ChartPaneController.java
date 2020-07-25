package jedrzejbronislaw.flowmeasure.view.chart;

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
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.SnapshotSaver;
import jedrzejbronislaw.flowmeasure.tools.loop.Refresher;
import jedrzejbronislaw.flowmeasure.view.chart.components.ChartOptions;
import lombok.Getter;
import lombok.Setter;

public class ChartPaneController implements Initializable {

	private static final int REFRESHING_TIME = 1000;
	
	public enum ValueUnit{
		Pulses, LitrePerSec
	}
	
	@FXML private Button refreshButton, saveButton;
	@FXML private BorderPane mainPane;
	@FXML private CheckBox lastSecsBox, liveBox;
	@FXML private RadioButton pulsesRadio, litresPerSecRadio;
	
	@Getter private LineChart<Number, Number> chart = newChart();
	@Setter private Consumer<ChartOptions> refreshButtonAction;
	
	private Refresher liveChartRefresher = new Refresher(REFRESHING_TIME, this::refresh);

	        
	public LineChart<Number, Number> newChart() {
		return new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane.setCenter(chart);
		
		refreshButton.disableProperty().bind(liveBox.selectedProperty());
		
		refreshButton.setOnAction(this::refresh);
		saveButton.setOnAction(e -> SnapshotSaver.withFileChooser(chart));
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
				.unit(getValueUnit())
				.build();
	}
	
	private boolean isLastSeconds() {
		return lastSecsBox.isSelected();
	}

	private ValueUnit getValueUnit(){
		if(pulsesRadio.isSelected())       return ValueUnit.Pulses;
		if(litresPerSecRadio.isSelected()) return ValueUnit.LitrePerSec;
		return null;
	}
}
