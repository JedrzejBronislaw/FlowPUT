package jedrzejbronislaw.flowmeasure.view.flowPreview;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverter;
import lombok.Setter;

public class FlowPreviewController implements Initializable {

	private static final DecimalFormat precisionFormat = new DecimalFormat("#.###");
	
	@FXML private Label label, pulsesLabel, secPulsesLabel;

	@Setter private FlowConverter flowconverter;
	
	private int pulses = 0;
	
	
	public void setNumber(int number) {
		label.setText("Flow " + (number+1));
	}
	
	public void addPulses(int value) {
		pulses += value;
		
		Platform.runLater(() -> {
			if(flowconverter != null) {
				pulsesLabel.setText(computeFlow(value));
				secPulsesLabel.setText(computeVolume(pulses));
			} else {
				pulsesLabel.setText(Integer.toString(value));
				secPulsesLabel.setText(Integer.toString(pulses));
			}
		});
	}

	private String computeFlow(int value) {
		Float litrePerSec = flowconverter.pulsesToLitrePerSec(value);
		if(litrePerSec != null)
			return precisionFormat.format(litrePerSec) + " " + FlowConverter.FLOW_UNIT; else
			return "";
	}

	private String computeVolume(int value) {
		return precisionFormat.format(flowconverter.pulsesToLitre(value)) + " " + FlowConverter.VOLUME_UNIT;
	}
	
	public int resetSecPulse() {
		int val = pulses;
		pulses = 0;
		return val;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
