package jedrzejbronislaw.flowmeasure.view.universal.live.preview;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;

public class LivePreview extends HBox implements Initializable {

	private static final DecimalFormat PRECISION_FORMAT = new DecimalFormat("#.###");
	
	@FXML private Label name, value, unit;

	
	public LivePreview(String unit) {
		MyFXMLLoader.create("LivePreview.fxml", this);
		setUnit(unit);
		setName("");
	}

	
	public void setName(String newName) {
		name.setText(newName + ":");
	}
	
	public void setUnit(String newUnit) {
		unit.setText(newUnit);
	}
	
	public void setValue(float newValue) {
		Platform.runLater(() -> {
			value.setText(PRECISION_FORMAT.format(newValue));
		});
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
