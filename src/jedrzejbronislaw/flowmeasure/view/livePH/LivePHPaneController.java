package jedrzejbronislaw.flowmeasure.view.livePH;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;

public class LivePHPaneController implements Initializable {

	@FXML private Label pHLabel;
	@FXML private Label conductivityLabel;
	@FXML private Label currentLabel;

	
	public void setViewMediator(ViewMediator viewMediator) {
		viewMediator.setFlowPreviewer(0, val -> this.refreshValue(val,           pHLabel));
		viewMediator.setFlowPreviewer(1, val -> this.refreshValue(val, conductivityLabel));
		viewMediator.setFlowPreviewer(2, val -> this.refreshValue(val,      currentLabel));
	}
	
	private void refreshValue(Integer value, Label label) {
		Float fvalue = (float) (value/100.0);
		Platform.runLater(() -> label.setText(fvalue.toString()));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
