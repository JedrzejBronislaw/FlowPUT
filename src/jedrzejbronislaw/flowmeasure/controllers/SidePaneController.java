package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.tools.Delay;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import lombok.Getter;
import lombok.Setter;

public class SidePaneController implements Initializable, EventListener, StateListener<ProcessState>{
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final int blikDiodeDuration = 100;
	
	private static final Color colorDiodeOn = Color.GREENYELLOW;
	private static final Color colorDiodeOff = Color.gray(0.7);
	private static final RadialGradient gradientDiodeOn  = createGradient(colorDiodeOn);
	private static final RadialGradient gradientDiodeOff = createGradient(colorDiodeOff);
	
	@FXML
	private Button saveButton, closeButton, startButton, endButton;
	
	@FXML
	@Getter
	private Label processStateLabel, startLabel, endLabel, durationLabel;
	
	@FXML
	private Circle receiverDiode;
	
	
	@Setter
	private Runnable saveButtonAction;
	@Setter
	private Runnable closeButtonAction;
	@Setter
	private Runnable startButtonAction;
	@Setter
	private Runnable endButtonAction;

	private ProcessState processState;
	private LocalDateTime startTime = null;
	private LocalDateTime endTime = null;
	
	private static RadialGradient createGradient(Color color) {
		return new RadialGradient(
				0,
				0,
				0.25,
				0.25,
				1,
				true,
				CycleMethod.NO_CYCLE,
				new Stop(0, color),
				new Stop(1, Color.WHITE));
	}
	
	public void diodeBlink() {
		diodeON();
		Delay.action(blikDiodeDuration, () -> diodeOFF());
	}
	
	private void diodeON() {
		Platform.runLater(() ->
			receiverDiode.setFill(gradientDiodeOn));
	}
	private void diodeOFF() {
		Platform.runLater(() ->
			receiverDiode.setFill(gradientDiodeOff));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		receiverDiode.setFill(gradientDiodeOff);
		
		saveButton.setOnAction(e  -> Injection.run(saveButtonAction));
		closeButton.setOnAction(e -> Injection.run(closeButtonAction));
		startButton.setOnAction(e -> Injection.run(startButtonAction));
		endButton.setOnAction(e   -> Injection.run(endButtonAction));
	}
	
	private void setEnable(Button button, boolean enable) {
		button.setDisable(!enable);
	}
	
	private void setDurationTimeLabel(LocalDateTime start, LocalDateTime end) {
		String time = TimeCalc.createDurationString(start, end,"\n") + "\n" +
				"(" + TimeCalc.durationSeconds(start, end) + " sec)";

		Platform.runLater(() -> durationLabel.setText(time));
	}
	private void setStartTimeLabel(String time) {
		Platform.runLater(() ->
			startLabel.setText(time));
	}
	private void setEndTimeLabel(String time) {
		Platform.runLater(() ->
			endLabel.setText(time));
	}
	
	@Override
	public void event(EventType event) {
		if(event == EventType.ReceivedData) {
			diodeBlink();
			if(processState == ProcessState.Ongoing)
				setDurationTimeLabel(startTime, LocalDateTime.now());
			else if (endTime != null)
				setDurationTimeLabel(startTime, endTime);
		}

		if(event == EventType.Process_Starts) {
			startTime = LocalDateTime.now();
			endTime = null;
			setStartTimeLabel(startTime.format(formatter));
			setEndTimeLabel("");
		}
		
		if(event == EventType.Process_Ends) {
			endTime = LocalDateTime.now();
			setEndTimeLabel(endTime.format(formatter));
		}
	}
	
	@Override
	public void onChangeState(ProcessState state) {
		processState = state;
		Platform.runLater(() ->
			processStateLabel.setText(state.toString()));
		
		setEnable(startButton, state == ProcessState.Before);
		setEnable(endButton,   state == ProcessState.Ongoing);
		
		setEnable(saveButton,  state != ProcessState.Before);
		setEnable(closeButton, state == ProcessState.Finished);
	}
}
