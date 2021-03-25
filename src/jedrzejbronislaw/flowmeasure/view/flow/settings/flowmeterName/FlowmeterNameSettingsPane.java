package jedrzejbronislaw.flowmeasure.view.flow.settings.flowmeterName;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;

public class FlowmeterNameSettingsPane extends HBox implements Initializable {
	
	@FXML private Label factoryNameLabel;
	@FXML private TextField nameField;

	
	public FlowmeterNameSettingsPane(String factoryName) {
		MyFXMLLoader.create("FlowmeterNameSettingsPane.fxml", this);
		setFactoryName(factoryName);
	}
	

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
