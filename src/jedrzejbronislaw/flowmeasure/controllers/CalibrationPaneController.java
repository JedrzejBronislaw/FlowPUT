package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.IntStream;

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
	private Button newMeasureButton;

	@FXML
	private ComboBox<Integer> flowmeterField;
	
	@FXML
	private Label flowLabel;
	
	@FXML
	private Label aveFlowLabel;
	

	@Setter
	private Consumer<Integer> start;
	@Setter
	private Runnable stop, reset, set;
	@Setter
	private Runnable newMeasure;
	@Setter
	private Consumer<Integer> onChangeFlowmeter;
	

	public void setCurrentValues(List<Integer> values) {
		Platform.runLater(() -> flowLabel.setText(formatValues(values)));
	}
	
	public void setCurrentAveValue(float value) {
		Platform.runLater(() -> aveFlowLabel.setText(Float.toString(value)));
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		IntStream.range(1, 7).forEach(flowmeterField.getItems()::add);
		flowmeterField.getSelectionModel().select(0);
		flowmeterField.setOnAction(e -> Injection.run(onChangeFlowmeter, flowmeterField.getValue()));
		
		startButton     .setOnAction(e -> Injection.run(start, flowmeterField.getValue()));
		resetButton     .setOnAction(e -> Injection.run(reset));
		stopButton      .setOnAction(e -> Injection.run(stop));
		setButton       .setOnAction(e -> Injection.run(set));
		newMeasureButton.setOnAction(e -> Injection.run(newMeasure));
	}

	private void setEnableComponens(State state) {
		mainVbox.setDisable(state == State.unavailable);
		
		boolean ongoing = (state == State.ongoing);

		startButton     .setDisable( ongoing);
		stopButton      .setDisable(!ongoing);
		flowmeterField  .setDisable( ongoing);
		newMeasureButton.setDisable(!ongoing);
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
	
	private String formatValues(List<Integer> value) {
		StringBuffer sb = new StringBuffer();
		
		for (int i=0; i<value.size()-1; i++) {
			sb.append(value.get(i));
			sb.append(System.lineSeparator());
		}
		sb.append(value.get(value.size()-1));
		
		String strValues = sb.toString();
		return strValues;
	}
}
