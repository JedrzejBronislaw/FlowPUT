package jedrzejbronislaw.flowmeasure.view.live;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class LivePaneController implements Initializable {

	@FXML private VBox flowBox;
	@FXML private Button resetButton;
	
	@Setter private Runnable resetAction;

	
	public void addFlowPreview(Node node) {
		flowBox.getChildren().add(node);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		resetButton.setOnAction(e -> Injection.run(resetAction));
	}
}
