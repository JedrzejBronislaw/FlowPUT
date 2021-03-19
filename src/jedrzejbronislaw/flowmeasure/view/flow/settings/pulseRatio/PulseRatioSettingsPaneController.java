package jedrzejbronislaw.flowmeasure.view.flow.settings.pulseRatio;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PulseRatioSettingsPaneController implements Initializable {
	
	@FXML private Label nameLabel;
	@FXML private TextField valueField;
	

	public void setName(String name) {
		nameLabel.setText(name + ":");
	}
	
	public void setValue(float value) {
		valueField.setText(Float.toString(value));
	}
	
	public float getValue() {
		return Float.parseFloat(valueField.getText());
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
