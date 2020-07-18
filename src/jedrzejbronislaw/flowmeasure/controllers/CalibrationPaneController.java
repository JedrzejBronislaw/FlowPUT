package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class CalibrationPaneController implements Initializable, EventListener {

	enum State {
		unavailable, available, ongoing
	}

	@FXML
	private VBox mainVbox;
	
	@FXML
	private Button startButton, resetButton, stopButton, setButton;

	@FXML
	private ComboBox<String> flowmeterField;
	
	@FXML
	private Label flowLabel;
	
	
	@Setter
	private Runnable start,stop, reset, set;
	

	public void setCurrentValue(int value) {
		Platform.runLater(() -> flowLabel.setText(Integer.toString(value)));
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		flowmeterField.getItems().addAll("1","2","3","4","5","6");
		
		startButton.setOnAction(e -> Injection.run(start));
		resetButton.setOnAction(e -> Injection.run(reset));
		stopButton .setOnAction(e -> Injection.run(stop));
		setButton  .setOnAction(e -> Injection.run(set));
	}

	private void setEnableComponens(State state) {
		mainVbox.setDisable(state == State.unavailable);
		
		boolean ongoing = (state == State.ongoing);

		startButton   .setDisable( ongoing);
		stopButton    .setDisable(!ongoing);
		flowmeterField.setDisable( ongoing);
	}

	@Override
	public void event(EventType event) {

		if (event.isOneOf(
				EventType.Close_Process,
				EventType.ConnectionSuccessful,
				EventType.Calibration_Ends))
			setEnableComponens(State.available);
		else if (event.isOneOf(
				EventType.Process_Starts,
				EventType.Process_Ends,
				EventType.ConnectionFailed,
				EventType.LostConnection))
			setEnableComponens(State.unavailable);
		else if (event == EventType.Calibration_Starts)
			setEnableComponens(State.ongoing);
	}
}
