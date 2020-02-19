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
import jedrzejbronislaw.flowmeasure.services.EventListener;
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
		
		startButton.setOnAction(e -> {
			if (start != null)
				start.run();
		});
		
		resetButton.setOnAction(e -> {
			if (reset != null)
				reset.run();
		});
		
		stopButton.setOnAction(e -> {
			if (stop != null)
				stop.run();
		});
		
		setButton.setOnAction(e -> {
			if (set != null)
				set.run();
		});
	}

	private void setEnableComponens(State state) {
		boolean ongoing;
		mainVbox.setDisable(state == State.unavailable);
		
		ongoing = (state == State.ongoing);

		startButton.setDisable(ongoing);
		flowmeterField.setDisable(ongoing);

		stopButton.setDisable(!ongoing);
//		resetButton.setDisable(!ongoing);
//		setButton.setDisable(!ongoing);
		
	}

	@Override
	public void event(EventType event) {

		if (event.isOneOf(
				EventType.Process_Ends,
				EventType.ConnectionSuccessful,
				EventType.Calibration_Ends))
			setEnableComponens(State.available);
		else if (event.isOneOf(
				EventType.Process_Starts,
				EventType.ConnectionFailed,
				EventType.LostConnection))
			setEnableComponens(State.unavailable);
		else if (event == EventType.Calibration_Starts)
			setEnableComponens(State.ongoing);
	}
}
