package jedrzejbronislaw.flowmeasure.view.settings;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import lombok.Setter;

public class SettingsPaneController implements Initializable, StateListener<ApplicationState> {
	
	@FXML private VBox mainBox;
	@FXML private VBox ratioBox;
	@FXML private VBox namesBox;
	@FXML private TextField bufferSizeField;
	@FXML private Button saveButton;
	@FXML private CheckBox bufferCheckbox;
	
	@Setter private Runnable savingAction;


	public void addRatioPane(Node pane) {
		ratioBox.getChildren().add(pane);
	}
	
	public void addNamePane(Node pane) {
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
		
		saveButton.setOnAction(e -> Injection.run(savingAction));
	}
	
	@Override
	public void onChangeState(ApplicationState state) {
		mainBox.setDisable(state == ApplicationState.PROCESS);
	}
}
