package jedrzejbronislaw.flowmeasure.view.calibration;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class CalibrationPaneController implements Initializable, AllStatesListener {

	enum InternalState {
		UNAVAILABLE, AVAILABLE, ONGOING
	}
	
	private static final DecimalFormat PRECISION_FORMAT = new DecimalFormat("#.###");

	@FXML private VBox mainVbox;
	@FXML private Button startButton, resetButton, stopButton, saveButton;
	@FXML private Button newMeasureButton;
	@FXML private ComboBox<String> flowmeterField;
	@FXML private Label flowLabel;
	@FXML private Label aveFlowLabel;
	
	@Setter private Consumer<Integer> start;
	@Setter private Runnable stop, reset, save;
	@Setter private Runnable newMeasure;
	@Setter private Consumer<Integer> onChangeFlowmeter;
	

	public void setCurrentValues(List<Integer> values) {
		Platform.runLater(() -> flowLabel.setText(formatValues(values)));
	}
	
	public void setCurrentAveValue(float value) {
		Platform.runLater(() -> aveFlowLabel.setText(PRECISION_FORMAT.format(value)));
	}
	
	public void setFlowmeterNames(List<String> names) {
		flowmeterField.getItems().clear();
		flowmeterField.getItems().addAll(names);
		flowmeterField.getSelectionModel().select(0);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		flowmeterField.getSelectionModel().select(0);
		flowmeterField.setOnAction(e -> Injection.run(onChangeFlowmeter, selectedFlowmeter()));
		
		startButton     .setOnAction(e -> Injection.run(start, selectedFlowmeter()));
		resetButton     .setOnAction(e -> Injection.run(reset));
		stopButton      .setOnAction(e -> Injection.run(stop));
		saveButton      .setOnAction(e -> Injection.run(save));
		newMeasureButton.setOnAction(e -> Injection.run(newMeasure));
	}

	private void setComponens(InternalState state) {
		mainVbox.setDisable(state == InternalState.UNAVAILABLE);
		
		boolean ongoing = (state == InternalState.ONGOING);

		startButton     .setDisable( ongoing);
		stopButton      .setDisable(!ongoing);
		saveButton      .setDisable(!ongoing);
		resetButton     .setDisable(!ongoing);
		flowmeterField  .setDisable( ongoing);
		newMeasureButton.setDisable(!ongoing);
		
		if (!ongoing)
			Platform.runLater(() -> {
				flowLabel.setText("");
				aveFlowLabel.setText("");
			});
	}
	
	@Override
	public void onChangeState(AllStates state) {
		if (!state.is(ConnectionState.CONNECTED))    setComponens(InternalState.UNAVAILABLE); else
		if ( state.is(ApplicationState.PROCESS))     setComponens(InternalState.UNAVAILABLE); else
		if ( state.is(ApplicationState.IDLE))        setComponens(InternalState.AVAILABLE);   else
		if ( state.is(ApplicationState.CALIBRATION)) setComponens(InternalState.ONGOING);
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

	private int selectedFlowmeter() {
		return flowmeterField.getSelectionModel().getSelectedIndex() + 1;
	}
}
