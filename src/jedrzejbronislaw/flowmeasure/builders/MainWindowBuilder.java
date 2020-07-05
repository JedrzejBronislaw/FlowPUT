package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.controllers.MainWindowController;
import lombok.Getter;

public class MainWindowBuilder extends Builder<MainWindowController> {

	@Getter private String fxmlFilePath = "MainWindow.fxml";

	@Override
	void afterBuild() {}
}
