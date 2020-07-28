package jedrzejbronislaw.flowmeasure.view.live;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class LivePaneController implements Initializable {

	@FXML private VBox flowBox;

	
	public void addFlowPreview(Node node) {
		flowBox.getChildren().add(node);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
