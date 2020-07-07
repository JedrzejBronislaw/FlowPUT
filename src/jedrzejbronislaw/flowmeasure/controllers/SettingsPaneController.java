package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class SettingsPaneController implements Initializable {
	
	@FXML
	private TextField pulsesPerLitre, bufferSizeField;
	
	@FXML
	private Button save;
	
	@FXML
	private CheckBox bufferCheckbox;
	
	@Setter
	private Runnable savingAction;

	private boolean activeUpdating = true;

	public float getPulsesPerLitre() {
		return Float.parseFloat(pulsesPerLitre.getText());
	}
	public int getBufferSize() {
		return Integer.parseInt(bufferSizeField.getText());
	}
	public boolean isSelectedBuffer() {
		return bufferCheckbox.isSelected();
	}
	
	public void setSettings(Settings settings) {
		if (!activeUpdating ) return;
		
		pulsesPerLitre.setText(Float.toString(settings.getPulsePerLitre()));
		bufferCheckbox.setSelected(settings.isBufferedData());
		bufferSizeField.setText(Integer.toString(settings.getBufferInterval()));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		bufferSizeField.disableProperty().bind(bufferCheckbox.selectedProperty().not());
		
		save.setOnAction(e -> Injection.run(savingAction));
	}
	
	public void suspendUpdating() {
		activeUpdating = false;
	}
	
	public void resumeUpdating() {
		activeUpdating = true;
	}

}
