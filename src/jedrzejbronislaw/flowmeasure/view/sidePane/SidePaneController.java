package jedrzejbronislaw.flowmeasure.view.sidePane;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.tools.Delay;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;
import lombok.Setter;

public class SidePaneController implements Initializable, EventListener, AllStatesListener {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final int blikDiodeDuration = 100;
	
	private static final Color colorDiodeOn  = Color.GREENYELLOW;
	private static final Color colorDiodeOff = Color.gray(0.7);
	private static final RadialGradient gradientDiodeOn  = createGradient(colorDiodeOn);
	private static final RadialGradient gradientDiodeOff = createGradient(colorDiodeOff);
	
	
	@FXML private VBox controlBox, onOffBox;
	@FXML private Label processStateLabel, startLabel, endLabel, durationLabel;
	@FXML private Button saveButton, closeButton, startButton, endButton;
	@FXML private Circle receiverDiode;
	
	@Setter private Runnable saveButtonAction;
	@Setter private Runnable closeButtonAction;
	@Setter private Runnable startButtonAction;
	@Setter private Runnable endButtonAction;

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
		onOffBox.setDisable(true);
		receiverDiode.setFill(gradientDiodeOff);
		
		saveButton.setOnAction(e  -> Injection.run(saveButtonAction));
		closeButton.setOnAction(e -> Injection.run(closeButtonAction));
		startButton.setOnAction(e -> Injection.run(startButtonAction));
		endButton.setOnAction(e   -> Injection.run(endButtonAction));
	}

	private void setEnable(Node node, boolean enable) {
		node.setDisable(!enable);
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
			setDurationTimeLabel(startTime, endTime);
		}
	}
	
	@Override
	public void onChangeState(AllStates state) {
		processState = state.getProcState();
		Platform.runLater(() ->
			processStateLabel.setText(state.getProcState().toString()));
		
		setEnable(startButton, state.is(ProcessState.Before));
		setEnable(endButton,   state.is(ProcessState.Ongoing));
		
		setEnable(saveButton, !state.is(ProcessState.Before));
		setEnable(closeButton, state.is(ProcessState.Finished));
		
		setEnable(onOffBox,    state.is(ConnectionState.Connected) && state.is(ApplicationState.Idle, ApplicationState.Process));
	}
}
