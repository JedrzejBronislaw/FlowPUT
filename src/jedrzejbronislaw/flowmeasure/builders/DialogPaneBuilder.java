package jedrzejbronislaw.flowmeasure.builders;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import jedrzejbronislaw.flowmeasure.controllers.DialogPaneController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DialogPaneBuilder extends Builder<DialogPaneController> {

	@Getter private String fxmlFilePath = "DialogPane.fxml";

	private final Pane root;
	private final String title;
	private final String content;
	private final int closeDelay;
	

	@Override
	void afterBuild() {
		((Pane)node).setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,CornerRadii.EMPTY, Insets.EMPTY)));
		controller.setNode(node);
		controller.setMessage(title, content);
		Platform.runLater(() -> 
			root.getChildren().add(node)
		);
		controller.close(closeDelay);
	}
}
