package jedrzejbronislaw.flowmeasure.view.liveED;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import jedrzejbronislaw.flowmeasure.components.ValueConverter;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;

public class LiveEDPaneController implements Initializable {

	@FXML private Label pHLabel;
	@FXML private Label conductivityLabel;
	@FXML private Label currentLabel;

	
	public void setViewMediator(ViewMediator viewMediator) {
		viewMediator.setFlowPreviewer(0, this::refreshPH);
		viewMediator.setFlowPreviewer(1, this::refreshEC);
		viewMediator.setFlowPreviewer(2, this::refreshAM);
	}
	
	private void refreshPH(Integer value) {
		float ph = ValueConverter.valueToPH(value/10f);
		Platform.runLater(() -> pHLabel.setText(String.format("%.2f", ph)));
	}
	
	private void refreshEC(Integer value) {
		float voltage = ValueConverter.valueToVoltage(value/10f);
		Platform.runLater(() -> conductivityLabel.setText(String.format("%.2f", voltage)));
	}
	
	private void refreshAM(Integer value) {
		float ampere = ValueConverter.valueToAmpere(value/10f);
		Platform.runLater(() -> currentLabel.setText(String.format("%.2f", ampere)));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
