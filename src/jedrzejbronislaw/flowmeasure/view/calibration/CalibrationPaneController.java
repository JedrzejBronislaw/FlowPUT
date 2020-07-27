package jedrzejbronislaw.flowmeasure.view.calibration;

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
import javafx.util.StringConverter;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class CalibrationPaneController implements Initializable, AllStatesListener {

	enum InternalState {
		unavailable, available, ongoing
	}
	
	public static final String DEF_FLOWMETER_NAME = "Flowmeter";

	@FXML private VBox mainVbox;
	@FXML private Button startButton, resetButton, stopButton, setButton;
	@FXML private Button newMeasureButton;
	@FXML private ComboBox<Integer> flowmeterField;
	@FXML private Label flowLabel;
	@FXML private Label aveFlowLabel;
	
	@Setter private Consumer<Integer> start;
	@Setter private Runnable stop, reset, set;
	@Setter private Runnable newMeasure;
	@Setter private Consumer<Integer> onChangeFlowmeter;
	

	public void setCurrentValues(List<Integer> values) {
		Platform.runLater(() -> flowLabel.setText(formatValues(values)));
	}
	
	public void setCurrentAveValue(float value) {
		Platform.runLater(() -> aveFlowLabel.setText(Float.toString(value)));
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		IntStream.range(0, Consts.FLOWMETERS_NUMBER).map(x -> x+1).forEach(flowmeterField.getItems()::add);
		flowmeterField.setConverter(createFlowmeterNameConverter());
		flowmeterField.getSelectionModel().select(0);
		flowmeterField.setOnAction(e -> Injection.run(onChangeFlowmeter, flowmeterField.getValue()));
		
		startButton     .setOnAction(e -> Injection.run(start, flowmeterField.getValue()));
		resetButton     .setOnAction(e -> Injection.run(reset));
		stopButton      .setOnAction(e -> Injection.run(stop));
		setButton       .setOnAction(e -> Injection.run(set));
		newMeasureButton.setOnAction(e -> Injection.run(newMeasure));
	}

	private void setComponens(InternalState state) {
		mainVbox.setDisable(state == InternalState.unavailable);
		
		boolean ongoing = (state == InternalState.ongoing);

		startButton     .setDisable( ongoing);
		stopButton      .setDisable(!ongoing);
		setButton       .setDisable(!ongoing);
		resetButton     .setDisable(!ongoing);
		flowmeterField  .setDisable( ongoing);
		newMeasureButton.setDisable(!ongoing);
		
		if (!ongoing) {
			flowLabel.setText("");
			aveFlowLabel.setText("");
		}
	}
	
	@Override
	public void onChangeState(AllStates state) {
		if (!state.is(ConnectionState.Connected))    setComponens(InternalState.unavailable); else
		if ( state.is(ApplicationState.Process))     setComponens(InternalState.unavailable); else
		if (!state.is(ProcessState.Before))          setComponens(InternalState.unavailable); else
		if ( state.is(ApplicationState.Idle))        setComponens(InternalState.available);   else
		if ( state.is(ApplicationState.Calibration)) setComponens(InternalState.ongoing);
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
	
	private StringConverter<Integer> createFlowmeterNameConverter() {
		return new StringConverter<Integer>() {
			
			@Override
			public String toString(Integer flowmeterNumber) {
				return DEF_FLOWMETER_NAME + " " + flowmeterNumber;
			}
			
			@Override
			public Integer fromString(String string) {return null;}
		};
	}
}
