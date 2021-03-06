package jedrzejbronislaw.flowmeasure.view.dialog;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DialogPaneBuilder extends Builder<DialogPaneController> {

	@Getter private String fxmlFilePath = "DialogPane.fxml";

	private final Pane root;
	

	@Override
	protected void afterBuild() {
		((Pane)node).setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY, Insets.EMPTY)));
		controller.setNode(node);
		controller.setRoot(root);
	}
}
