package jedrzejbronislaw.flowmeasure.view.flow.settings.pulseRatio;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;

public class PulseRatioSettingsPane extends HBox implements Initializable {
	
	@FXML private Label nameLabel;
	@FXML private TextField valueField;

	
	public PulseRatioSettingsPane(String name) {
		MyFXMLLoader2.create("PulseRatioSettingsPane.fxml", this);
		setName(name);
	}
	

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
