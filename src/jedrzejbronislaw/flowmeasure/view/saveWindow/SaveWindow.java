package jedrzejbronislaw.flowmeasure.view.saveWindow;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.SavingService;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.SaveAction;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import lombok.Setter;

public class SaveWindow {

	private final static String WINDOW_TITLE = "Saving measurement...";
	private final static String CSS_FILE_NAME = "application.css";
	
	private static final String LOGO_FILE_NAME = "logo.png";
	

	private ResourceAccess resources;
	private ProcessRepository process;
	private SavingService savingService;

	@Setter private SaveAction saveAction;
	
	private Stage stage = new Stage();
	private SaveWindowPane saveWindowPane;
	
	
	public void setOwner(Window owner) {
		stage.initOwner(owner);
	}
	
	
	public SaveWindow() {
		Components.getComponentsLoader().addLoadMethod(() -> {
			resources = Components.getResources();
			process = Components.getRepository().getCurrentProcessRepository();
			savingService = Components.getSavingService();
			
			saveWindowPane = new SaveWindowPane();
			saveWindowPane.setExitAction(stage::close);
			saveWindowPane.setSaveAction(this::save);
			
			buildStage();
		});
	}
	
	
	private void buildStage() {
		Scene scene = new Scene(saveWindowPane);
		scene.getStylesheets().add(resources.getResourcePath(CSS_FILE_NAME));
		
		stage.getIcons().add(loadLogo());
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setScene(scene);
		stage.setTitle(WINDOW_TITLE);

		Platform.runLater(() -> stage.sizeToScene());
	}

	private Image loadLogo() {
		return new Image(resources.path(LOGO_FILE_NAME));
	}

	private void save(ProcessRepositoryWriterOptions options, boolean openFile) {
		File file = savingService.openFileChooser(stage);
		if (file == null) return;
		
		saveAction.save(process, file, options);
		if (openFile) tryOpenFileInDefaultApplication(file);
	}

	private void tryOpenFileInDefaultApplication(File f) {
		if (f.exists() && Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showWindow() {
		if (stage != null) stage.show();
	}
}
