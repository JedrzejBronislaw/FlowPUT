package jedrzejbronislaw.flowmeasure.builders;

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
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.controllers.SaveWindowController;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.SaveAction;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader.NodeAndController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class SaveWindowBuilder {

//	private double WINDOW_WIDTH = 500;
//	private double WINDOW_HEIGHT = 200;

	@NonNull
	private ResourcesRepository resources;
	@NonNull
	private ProcessRepository process;

	private File initialDirectory = null;
	
	@Setter
	private SaveAction saveAction;
	
	@Setter
	private Consumer<File> chooseFileAction;
	
	@Setter
	private Supplier<String> fileNamer;
	
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
		
		fileChooser.setTitle("Saving process data...");
		fileChooser.setInitialFileName(Injection.get(fileNamer,""));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Coma Separted Value", ".csv"));
		if(initialDirectory != null)
			fileChooser.setInitialDirectory(initialDirectory);
		
		file = fileChooser.showSaveDialog(null);
		
		if(file != null)
			Injection.run(chooseFileAction, file);

		return file;
	}
		
	public Stage build() {
		Parent root;
		
		MyFXMLLoader<SaveWindowController> loader = new MyFXMLLoader<>();
		NodeAndController<SaveWindowController> nac = loader.create("SaveMeasurementWindow.fxml");
		root = (Parent) nac.getNode();
		SaveWindowController controller = nac.getController();
		
		Scene scene = new Scene(root);//, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.getStylesheets().add(resources.getResourcePath("application.css"));
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Saving measurement...");
		stage.show();
		controller.setExitAction(() -> {
			stage.close();
		});
			
		controller.setSaveAction(options -> {
			File f = choosingFileAction();
			if(f == null) return;
			
			saveAction.save(process, f, options);
			
			if(Desktop.isDesktopSupported()) {
				if(f.exists()) {
					try {
						Desktop.getDesktop().open(f);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
			
		return stage;
	}
}
