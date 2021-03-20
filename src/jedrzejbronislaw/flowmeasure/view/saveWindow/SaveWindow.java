package jedrzejbronislaw.flowmeasure.view.saveWindow;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.SaveAction;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import lombok.NonNull;
import lombok.Setter;

public class SaveWindow {

	private final static String WINDOW_TITLE = "Saving measurement...";
	private final static String FILE_CHOOSER_TITLE = "Saving process data...";
	private final static String CSS_FILE_NAME = "application.css";
	
	private static final String LOGO_FILE_NAME = "logo.png";
	

	@NonNull private ResourceAccess resources;
	@NonNull private ProcessRepository process;
	@NonNull private Settings settings;

	@Setter private SaveAction saveAction;
	@Setter private Consumer<File> onFileChoose;
	@Setter private Supplier<String> fileNamer;
	        private File initialDirectory = null;
	
	private Stage stage = new Stage();
	private SaveWindowPane saveWindowPane;
	
	
	public void setOwner(Window owner) {
		stage.initOwner(owner);
	}
	
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
	
	
	public SaveWindow(ResourceAccess resources, ProcessRepository process, Settings settings) {
		this.resources = resources;
		this.process   = process;
		this.settings  = settings;
		
		saveWindowPane = new SaveWindowPane();
		
		saveWindowPane.setFlowmeterNames(getFlowmeterNames());
		saveWindowPane.setExitAction(stage::close);
		saveWindowPane.setSaveAction(this::save);
		
		buildStage();
	}
	
	
	private String[] getFlowmeterNames() {
		int size = Consts.FLOWMETERS_NUMBER;

		String[] names = new String[size];
		
		for(int i=0; i<size; i++)
			names[i] = settings.getString(new FlowmeterNameProperty(i));

		return names;
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
		File file = choosingFileAction();
		if(file == null) return;
		
		saveAction.save(process, file, options);
		if (openFile) tryOpenFileInDefaultApplication(file);
	}
	
	private File choosingFileAction(){
		FileChooser fileChooser = new FileChooser();
		File file;
		
		fileChooser.setTitle(FILE_CHOOSER_TITLE);
		fileChooser.setInitialFileName(Injection.get(fileNamer, ""));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Coma Separted Value", ".csv"));
		if(initialDirectory != null)
			fileChooser.setInitialDirectory(initialDirectory);
		
		file = fileChooser.showSaveDialog(stage);
		
		if(file != null) Injection.run(onFileChoose, file);

		return file;
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
	
	public void showWindow() {
		if (stage != null) stage.show();
	}
}
