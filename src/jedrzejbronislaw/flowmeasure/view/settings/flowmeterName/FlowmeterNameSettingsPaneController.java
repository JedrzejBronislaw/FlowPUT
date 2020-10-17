package jedrzejbronislaw.flowmeasure.view.settings.flowmeterName;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FlowmeterNameSettingsPaneController implements Initializable {
	
	@FXML private Label factoryNameLabel;
	@FXML private TextField nameField;
	

	public void setFactoryName(String name) {
		factoryNameLabel.setText(name);
	}
	
	public void setName(String name) {
		nameField.setText(name);
	}
	
	public String getName() {
		return nameField.getText();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
