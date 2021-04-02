package jedrzejbronislaw.flowmeasure.view.universal.live;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.view.universal.live.preview.LivePreview;

public class UniLivePane extends VBox implements Initializable {

	@FXML private VBox flowBox;
	@FXML private Button resetButton;
	
	private List<LivePreview> previews = new ArrayList<>();

	
	public UniLivePane() {
		MyFXMLLoader.create("UniLivePane.fxml", this);
		
		Components.getComponentsLoader().addLoadMethod(() -> {
			Components.getSettings().addChangeListener(this::updateNames);
		});
	}

	
	public void addFlowPreview(LivePreview preview) {
		previews.add(preview);
		flowBox.getChildren().add(preview);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		resetButton.setOnAction(e -> this.resetVolumes());
	}
	
	private void updateNames() {
		// TODO
	}

	private void resetVolumes() {
		// TODO
	}
}
