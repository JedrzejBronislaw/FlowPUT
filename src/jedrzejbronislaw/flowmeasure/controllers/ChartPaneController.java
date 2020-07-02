package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.Refresher;
import jedrzejbronislaw.flowmeasure.tools.SnapshotSaver;
import lombok.Getter;
import lombok.Setter;

public class ChartPaneController implements Initializable{

	public enum ValueUnit{
		Pulses, Litre, LitrePerSec
	}
	
	
	@FXML
	private Button refreshButton, saveButton;

	@FXML
	private BorderPane mainPane;

	@FXML
	private CheckBox lastSecsBox, liveBox;
	
	@FXML
	private RadioButton pulsesRadio, litresRadio, litresPerSecRadio;
	
	
	@Setter
	private Consumer<LineChart<Number, Number>> refreshButtonAction;
	
	@Getter
	private NumberAxis axisX, axisY;
	private LineChart<Number, Number> chart;
	
	
	private Refresher liveChartRefresher = new Refresher(1000, () ->  Injection.run(refreshButtonAction, chart));
	
	public boolean isLastSeconds() {
		return lastSecsBox.isSelected();
	}

	public ValueUnit getValueUnit(){
		if(pulsesRadio.isSelected())       return ValueUnit.Pulses;
		if(litresRadio.isSelected())       return ValueUnit.Litre;
		if(litresPerSecRadio.isSelected()) return ValueUnit.LitrePerSec;
		return null;
	}
	
	public ChartPaneController() {
		createChart();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane.setCenter(chart);
		
		refreshButton.disableProperty().bind(liveBox.selectedProperty());
		
		refreshButton.setOnAction(e -> Injection.run(refreshButtonAction, chart));
		saveButton.setOnAction(e -> SnapshotSaver.withFileChooser(chart));
		liveBox.setOnAction(e -> {
			if(liveBox.isSelected())
				liveChartRefresher.on();
			else
				liveChartRefresher.off();
		});
	}

	private void createChart() {
		axisX = new NumberAxis();
		axisY = new NumberAxis();
		axisX.setForceZeroInRange(false);
		
		chart = new LineChart<Number, Number>(axisX, axisY);
	}
}
