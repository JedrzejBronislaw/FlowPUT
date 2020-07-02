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
	private Runnable lastSecsBoxAction;

	@Getter
	private boolean lastSeconds;
	
	@Setter
	private Consumer<LineChart<Number, Number>> refreshButtonAction;
	
	@Getter
	private NumberAxis axisX, axisY;
	private LineChart<Number, Number> chart;
	
	private Refresher liveChartRefresher = new Refresher(1000, () ->  Injection.run(refreshButtonAction, chart));
	
	private void startRefresher() {
		liveChartRefresher.on();
	}
	
	private void stopRefresher() {
		liveChartRefresher.off();
	}

	public ValueUnit getValueUnit(){
		if(pulsesRadio.isSelected())
			return ValueUnit.Pulses;
		if(litresRadio.isSelected())
			return ValueUnit.Litre;
		if(litresPerSecRadio.isSelected())
			return ValueUnit.LitrePerSec;
		return null;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		axisX = new NumberAxis();
		axisY = new NumberAxis();
		chart = new LineChart<Number, Number>(axisX, axisY);
//		axisX.setAutoRanging(true);
//		axisY.setfobou setAutoRanging(true);
		axisX.setForceZeroInRange(false);

		
		refreshButton.disableProperty().bind(liveBox.selectedProperty());
		
		mainPane.setCenter(chart);
//		mainVbox.getChildren().add(0, chart);
		
		refreshButton.setOnAction(e -> Injection.run(refreshButtonAction, chart));
		saveButton.setOnAction(e -> SnapshotSaver.withFileChooser(chart));
		lastSecsBox.setOnAction(e -> Injection.run(lastSecsBoxAction));
		
		liveBox.setOnAction(e -> {
			if(liveBox.isSelected())
				startRefresher();
			else
				stopRefresher();
		});
		
		lastSecsBox.setOnAction(e -> lastSeconds = lastSecsBox.isSelected());

	}
}
