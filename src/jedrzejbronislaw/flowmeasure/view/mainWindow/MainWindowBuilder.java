package jedrzejbronislaw.flowmeasure.view.mainWindow;

import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;

public class MainWindowBuilder extends Builder<MainWindowController> {

	@Getter private String fxmlFilePath = "MainWindow.fxml";

	@Override
	protected void afterBuild() {}
}
