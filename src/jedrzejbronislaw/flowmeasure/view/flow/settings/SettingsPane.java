package jedrzejbronislaw.flowmeasure.view.flow.settings;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import jedrzejbronislaw.flowmeasure.view.flow.settings.flowmeterName.FlowmeterNameSettingsPane;
import jedrzejbronislaw.flowmeasure.view.flow.settings.pulseRatio.PulseRatioSettingsPane;

public class SettingsPane extends VBox implements Initializable, StateListener<ApplicationState> {

	private static final String RATIO_LABEL = "Ratio ";
	private static final String  NAME_LABEL = "Flowmeter ";
	
	@FXML private VBox ratioBox;
	@FXML private VBox namesBox;
	@FXML private TextField bufferSizeField;
	@FXML private Button saveButton;
	@FXML private CheckBox bufferCheckbox;

	private List<PulseRatioSettingsPane> ratios = new ArrayList<>();
	private List<FlowmeterNameSettingsPane> names = new ArrayList<>();
	
	private Settings settings;
	private boolean activeUpdating = true;

	
	public SettingsPane() {
		MyFXMLLoader2.create("SettingsPane.fxml", this);
		
		Components.getComponentsLoader().addLoadMethod(() -> {
			settings = Components.getSettings();
			settings.addChangeListener(this::updateSettings);
		});
	}


	public void addRatioPane(PulseRatioSettingsPane pane) {
		ratios.add(pane);
		ratioBox.getChildren().add(pane);
	}
	
	public void addNamePane(FlowmeterNameSettingsPane pane) {
		names.add(pane);
		namesBox.getChildren().add(pane);
	}
	
	public int getBufferSize() {
		return Integer.parseInt(bufferSizeField.getText());
	}

	public boolean isSelectedBuffer() {
		return bufferCheckbox.isSelected();
	}
	
	public void setSettings(Settings settings) {
		bufferCheckbox.setSelected(settings.getBool(AppProperties.BUFFERED_DATA));
		bufferSizeField.setText(settings.getPropertyValue(AppProperties.BUFFER_INTERVAL));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		bufferSizeField.disableProperty().bind(bufferCheckbox.selectedProperty().not());
		
		saveButton.setOnAction(e -> this.save());

		addRatioPanes(Consts.FLOWMETERS_NUMBER);
		addNamePanes( Consts.FLOWMETERS_NUMBER);
	}
	
	private void save() {
		activeUpdating = false;
		setSettings();
		activeUpdating = true;
		
		settings.saveToFile();
	}
	
	private void updateSettings() {
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			ratios.get(i).setName(settings.getString(new FlowmeterNameProperty(i)));
		
		if (!activeUpdating ) return;
		
		setSettings(settings);
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++) {
			ratios.get(i).setValue(settings.getFloat(new RatioProperty(i)));
			names.get(i).setName(settings.getString(new FlowmeterNameProperty(i)));
		}
	}
	
	private void addRatioPanes(int number) {
		for(int i=0; i<number; i++)
			addRatioPane(new PulseRatioSettingsPane(RATIO_LABEL + (i+1) + ":"));
	}
	
	private void addNamePanes(int number) {
		for(int i=0; i<number; i++)
			addNamePane(new FlowmeterNameSettingsPane(NAME_LABEL + (i+1) + ":"));
	}

	private void setSettings() {
		boolean isBuffer = isSelectedBuffer();
		int bufferSize   = getBufferSize();
		
		settings.setProperty(AppProperties.BUFFERED_DATA, isBuffer);
		settings.setProperty(AppProperties.BUFFER_INTERVAL, bufferSize);

		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			settings.setProperty(new RatioProperty(i), ratios.get(i).getValue());
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			settings.setProperty(new FlowmeterNameProperty(i), names.get(i).getName());
	}
	
	@Override
	public void onChangeState(ApplicationState state) {
		setDisable(state == ApplicationState.PROCESS);
	}
}
