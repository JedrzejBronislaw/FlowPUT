package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import lombok.Getter;
import lombok.Setter;

public class FlowPreviewController implements Initializable{

	@FXML
	private Label label, pulsesLabel, secPulsesLabel;

	@Getter
	private int number = 0;
	private int pulses = 0;
	
	@Setter
	private FlowConverter flowconverter;
	
	private DecimalFormat precisionFormat = new DecimalFormat("#.###");
	
	public void setNumber(int number) {
		this.number = number;
		label.setText("Flow " + (number+1));//TODO internationalization
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
			return precisionFormat.format(litrePerSec) + " " + FlowConverter.FLOW_UNIT;
		else
			return "";//computeVolume(value);
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
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
}
