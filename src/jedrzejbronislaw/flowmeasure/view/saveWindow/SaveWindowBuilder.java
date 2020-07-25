package jedrzejbronislaw.flowmeasure.view.saveWindow;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.SaveAction;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class SaveWindowBuilder extends Builder<SaveWindowController> {

	private final static String WINDOW_TITLE = "Saving measurement...";
	private final static String FILE_CHOOSER_TITLE = "Saving process data...";
	private final static String CSS_FILE_NAME = "application.css";
	
	
	@Getter private String fxmlFilePath = "SaveMeasurementWindow.fxml";


	@NonNull
	private ResourceAccess resources;
	@NonNull
	private ProcessRepository process;

	private File initialDirectory = null;
	
	@Setter
	private SaveAction saveAction;
	
	@Setter
	private Consumer<File> onFileChoose;
	
	@Setter
	private Supplier<String> fileNamer;
	
	private Stage stage;
	
	public boolean setInitialDirectory(String initialDirectory) {
		
		if (initialDirectory != null && !initialDirectory.isEmpty()) {
			File path = new File(initialDirectory);
			if(path.isDirectory()) {
				this.initialDirectory = path;
				return true;
			}
		}
		
		return false;
	}
	
	private File choosingFileAction(){
		FileChooser fileChooser = new FileChooser();
		File file;
		
		fileChooser.setTitle(FILE_CHOOSER_TITLE);
		fileChooser.setInitialFileName(Injection.get(fileNamer, ""));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Coma Separted Value", ".csv"));
		if(initialDirectory != null)
			fileChooser.setInitialDirectory(initialDirectory);
		
		file = fileChooser.showSaveDialog(null);
		
		if(file != null) Injection.run(onFileChoose, file);

		return file;
	}

	@Override
	protected void afterBuild() {
		buildStage();

		controller.setExitAction(stage::close);
		controller.setSaveAction(this::save);
	}

	private void save(ProcessRepositoryWriterOptions options, boolean openFile) {
		File file = choosingFileAction();
		if(file == null) return;
		
		saveAction.save(process, file, options);
		if (openFile) tryOpenFileInDefaultApplication(file);
	}

	private void tryOpenFileInDefaultApplication(File f) {
		if(f.exists() && Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void buildStage() {
		Scene scene = new Scene((Parent)node);
		scene.getStylesheets().add(resources.getResourcePath(CSS_FILE_NAME));
		
		stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(WINDOW_TITLE);
	}
	
	public void showWindow() {
		if (stage != null) stage.show();
	}
}
