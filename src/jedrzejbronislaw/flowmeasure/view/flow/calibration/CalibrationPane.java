package jedrzejbronislaw.flowmeasure.view.flow.calibration;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.calibration.Calibration;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;

public class CalibrationPane extends VBox implements Initializable, AllStatesListener {

	enum InternalState {
		UNAVAILABLE, AVAILABLE, ONGOING
	}
	
	private static final DecimalFormat PRECISION_FORMAT = new DecimalFormat("#.###");

	@FXML private Button startButton, resetButton, stopButton, saveButton;
	@FXML private Button newMeasureButton;
	@FXML private ComboBox<String> flowmeterField;
	@FXML private Label flowLabel;
	@FXML private Label aveFlowLabel;
	
	private EventManager eventManager;
	private FlowManager flowManager;
	private Settings settings;
	private Calibration calibration;

	private int calibratedFlowmeter;
	
	
	public CalibrationPane() {
		MyFXMLLoader2.create("CalibrationPane.fxml", this);
		
		Components.getComponentsLoader().addLoadMethod(() -> {
			eventManager = Components.getEventManager();
			flowManager  = Components.getFlowManager();
			settings     = Components.getSettings();
			calibration  = Components.getCalibration();
			init();
		});
	}
	
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
	
	private void updateFlowmeterNames() {
		List<String> names = new ArrayList<>(Consts.FLOWMETERS_NUMBER);
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			names.add(settings.getString(new FlowmeterNameProperty(i)));
		
		setFlowmeterNames(names);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	private void init() {
		flowmeterField.getSelectionModel().select(0);
		flowmeterField.setOnAction(e -> setFlowmeter(selectedFlowmeter()));
		
		startButton     .setOnAction(e -> startCalibration(selectedFlowmeter()));
		resetButton     .setOnAction(e -> calibration.reset());
		stopButton      .setOnAction(e -> stopCalibration());
		saveButton      .setOnAction(e -> saveCalibration());
		newMeasureButton.setOnAction(e -> calibration.newMeasure());

		calibration.setAveValueListener(value -> setCurrentAveValue(value));
		calibration.setValuesListener  (value -> setCurrentValues(value));
		
		settings.addChangeListener(this::updateFlowmeterNames);
	}

	private void setComponens(InternalState state) {
		setDisable(state == InternalState.UNAVAILABLE);
		
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
	
	
	private void startCalibration(int flowmeter) {
		setFlowmeter(flowmeter);
		if (event(EventType.CALIBRATION_STARTS)) switchFlow(FlowConsumerType.CALIBRATION);
	}
	
	private void stopCalibration() {
		if (event(EventType.CALIBRATION_ENDS))   switchFlow(FlowConsumerType.NONE);
	}
	
	private void saveCalibration() {
		settings.setProperty(new RatioProperty(calibratedFlowmeter-1), calibration.getAveValue());
		settings.saveToFile();
	}
	
	private boolean event(EventType event) {
		return eventManager.submitEvent(event);
	}

	private void switchFlow(FlowConsumerType flowConsumerType) {
		flowManager.setFlowConsumerType(flowConsumerType);
	}

	private void setFlowmeter(int flowmeter) {
		calibratedFlowmeter = flowmeter;
		calibration.setFlowmeter(flowmeter);
		calibration.reset();
	}
}
