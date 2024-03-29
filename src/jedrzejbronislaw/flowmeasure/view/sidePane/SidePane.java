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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.globalActions.ActionContainer;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.tools.Delay;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.tools.TextTools;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;

public class SidePane extends HBox implements Initializable, EventListener, AllStatesListener {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final int BLINK_DIODE_DURATION = 100;
	
	private static final Color COLOR_DIODE_ON  = Color.GREENYELLOW;
	private static final Color COLOR_DIODE_OFF = Color.gray(0.7);
	private static final RadialGradient GRADIENT_DIODE_ON  = createGradient(COLOR_DIODE_ON);
	private static final RadialGradient GRADIENT_DIODE_OFF = createGradient(COLOR_DIODE_OFF);
	
	
	@FXML private VBox controlBox;
	@FXML private HBox onOffBox;
	@FXML private Label processStateLabel, startLabel, endLabel, durationLabel;
	@FXML private Button saveButton, closeButton, beginButton, endButton;
	@FXML private Circle receiverDiode;
	
	private ActionContainer actions;

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

	
	public SidePane() {
		Components.getComponentsLoader().addLoadMethod(() -> {
			actions = Components.getGlobalActions();
		});
		
		MyFXMLLoader.create("SidePane.fxml", this);
	}
	
	
	public void diodeBlink() {
		diodeON();
		Delay.action(BLINK_DIODE_DURATION, () -> diodeOFF());
	}
	
	private void diodeON() {
		Platform.runLater(() ->
			receiverDiode.setFill(GRADIENT_DIODE_ON));
	}
	private void diodeOFF() {
		Platform.runLater(() ->
			receiverDiode.setFill(GRADIENT_DIODE_OFF));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		onOffBox.setDisable(true);
		receiverDiode.setFill(GRADIENT_DIODE_OFF);
		
		saveButton.setOnAction (e -> actions.saveProcess());
		closeButton.setOnAction(e -> actions.closeProcess());
		beginButton.setOnAction(e -> actions.startProcess());
		endButton.setOnAction  (e -> actions.endProcess());
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
		if (event == EventType.RECEIVED_DATA) {
			diodeBlink();
			if (processState == ProcessState.ONGOING)
				setDurationTimeLabel(startTime, LocalDateTime.now());
		}

		if (event == EventType.PROCESS_STARTS) {
			startTime = LocalDateTime.now();
			endTime = null;
			setStartTimeLabel(startTime.format(FORMATTER));
			setEndTimeLabel("");
		}
		
		if (event == EventType.PROCESS_ENDS) {
			endTime = LocalDateTime.now();
			setEndTimeLabel(endTime.format(FORMATTER));
			setDurationTimeLabel(startTime, endTime);
		}
		
		if (event == EventType.CLOSE_PROCESS) {
			Platform.runLater(() -> {
				startLabel   .setText("");
				endLabel     .setText("");
				durationLabel.setText("");
			});
		}
	}
	
	@Override
	public void onChangeState(AllStates state) {
		processState = state.getProcState();
		Platform.runLater(() ->
			processStateLabel.setText(TextTools.firstCharUpper(state.getProcState().toString())));
		
		setEnable(beginButton, state.is(ProcessState.BEFORE));
		setEnable(endButton,   state.is(ProcessState.ONGOING));
		
		setEnable(saveButton, !state.is(ProcessState.BEFORE));
		setEnable(closeButton, state.is(ProcessState.FINISHED));
		
		setEnable(onOffBox,    state.is(ConnectionState.CONNECTED) && state.is(ApplicationState.IDLE, ApplicationState.PROCESS));
	}
}
