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
import jedrzejbronislaw.flowmeasure.services.EventListener;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import lombok.Getter;
import lombok.Setter;

public class SidePaneController implements Initializable, EventListener, StateListener<ProcessState>{

	private static final int blikDiodeDuration = 100;
	
	private static final Color colorDiodeOn = Color.GREENYELLOW;
	private static final Color colorDiodeOff = Color.gray(0.7);
	private static final RadialGradient gradientDiodeOn = new RadialGradient(
			0,
            0,
            0.25,
            0.25,
            1,
            true,
            CycleMethod.NO_CYCLE,
            new Stop(0, colorDiodeOn),
            new Stop(1, Color.WHITE));

	private static final RadialGradient gradientDiodeOff = new RadialGradient(
			0,
            0,
            0.25,
            0.25,
            1,
            true,
            CycleMethod.NO_CYCLE,
            new Stop(0, colorDiodeOff),
            new Stop(1, Color.WHITE));


	
	@FXML
	private Button saveButton, startButton, endButton;
	
	@FXML
	@Getter
	private Label processStateLabel, startLabel, endLabel, durationLabel;
	
	@FXML
	private Circle receiverDiode;
	
	
	@Setter
	private Runnable saveButtonAction;
	@Setter
	private Runnable startButtonAction;
	@Setter
	private Runnable endButtonAction;

	private ProcessState processState;
	private LocalDateTime startTime = null;
	private LocalDateTime endTime = null;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	
	public void diodeBlink() {
		diodeON();
		new Thread(() -> {
			try {
				Thread.sleep(blikDiodeDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Platform.runLater(() -> diodeOFF());
		}).start();
	}
	
	private void diodeON() {
		receiverDiode.setFill(gradientDiodeOn);
	}
	private void diodeOFF() {
		receiverDiode.setFill(gradientDiodeOff);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		receiverDiode.setFill(gradientDiodeOff);
		
		saveButton.setOnAction(e  -> Injection.run(saveButtonAction));
		startButton.setOnAction(e -> Injection.run(startButtonAction));
		endButton.setOnAction(e   -> Injection.run(endButtonAction));
	}
	
	private void setButtonEnable(boolean processOngoing) {
		endButton.setDisable(!processOngoing);
		startButton.setDisable(processOngoing);
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
				setDurationTimeLabel(startTime.minusMinutes(61), LocalDateTime.now());
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
		processStateLabel.setText(state.toString());
		
		if(state == ProcessState.Ongoing)
			setButtonEnable(true);
		else if(state.compare().isOneOf(
					ProcessState.Before,
					ProcessState.Finished,
					ProcessState.Interrupted,
					ProcessState.LostConnection))
			setButtonEnable(false);
	}

}
