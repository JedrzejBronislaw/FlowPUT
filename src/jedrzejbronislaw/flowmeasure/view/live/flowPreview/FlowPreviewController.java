package jedrzejbronislaw.flowmeasure.view.live.flowPreview;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverter;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowUnit;
import jedrzejbronislaw.flowmeasure.components.flowConverter.VolumeUnit;
import lombok.Setter;

public class FlowPreviewController implements Initializable {

	private static final DecimalFormat PRECISION_FORMAT = new DecimalFormat("#.###");
	
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
			return PRECISION_FORMAT.format(litrePerSec) + " " + FlowUnit.LITRE_PER_SECOND; else
			return "";
	}

	private String computeVolume(int value) {
		return PRECISION_FORMAT.format(flowconverter.pulsesToLitre(value)) + " " + VolumeUnit.LITRE;
	}
	
	public int resetSecPulse() {
		int val = pulses;
		pulses = 0;
		return val;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
