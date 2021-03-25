package jedrzejbronislaw.flowmeasure.view.flow.live.flowPreview;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverter;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;

public class FlowPreview extends HBox implements Initializable {

	private static final DecimalFormat PRECISION_FORMAT = new DecimalFormat("#.###");
	
	@FXML private Label label, pulsesLabel, secPulsesLabel;

	private final FlowConverter flowconverter;
	
	private int pulses = 0;

	
	public FlowPreview(FlowConverter flowconverter) {
		MyFXMLLoader.create("FlowPreview.fxml", this);
		
		setName("");
		this.flowconverter = flowconverter;
	}

	
	public void setName(String name) {
		label.setText(name + ":");
	}
	
	public void addPulses(int value) {
		pulses += value;
		
		Platform.runLater(() -> {
			if (flowconverter != null) {
				pulsesLabel.setText(computeFlow(value));
				secPulsesLabel.setText(computeVolume(pulses));
			} else {
				pulsesLabel.setText(Integer.toString(value));
				secPulsesLabel.setText(Integer.toString(pulses));
			}
		});
	}

	private String computeFlow(int value) {
		Float litrePerSec = flowconverter.toFlow(value);
		if (litrePerSec == null) return "";

		return PRECISION_FORMAT.format(litrePerSec) + " " + flowconverter.getFlowUnit();
	}

	private String computeVolume(int value) {
		return PRECISION_FORMAT.format(flowconverter.toVolume(value)) + " " + flowconverter.getVolumeUnit();
	}
	
	public int resetSecPulse() {
		int val = pulses;
		pulses = 0;
		return val;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
