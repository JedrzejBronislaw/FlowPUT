package jedrzejbronislaw.flowmeasure.components;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.tools.fileNamer.FileNamer;
import jedrzejbronislaw.flowmeasure.tools.fileNamer.FileNamer1;

public class SavingService {
	
	private final static String FILE_CHOOSER_TITLE = "Saving process data...";
	
	
	public File openFileChooser(Window ownerWindow) {
		FileChooser fileChooser = new FileChooser();
		File initialDirectory = getInitialDirectory();
		File file;

		ProcessRepository process = Components.getRepository().getCurrentProcessRepository();
		FileNamer filenamer = new FileNamer1(process);
		
		fileChooser.setTitle(FILE_CHOOSER_TITLE);
		fileChooser.setInitialFileName(filenamer.createName());
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Coma Separted Value", ".csv"));
		if (initialDirectory != null)
			fileChooser.setInitialDirectory(initialDirectory);
		
		file = fileChooser.showSaveDialog(ownerWindow);
		
		if (file != null) saveSavingPath(file);

		return file;
	}
	
	private File getInitialDirectory() {
		String initialDirectory = Components.getSettings().getString(AppProperties.SAVE_PATH);
		
		if (initialDirectory != null && !initialDirectory.isEmpty()) {
			File path = new File(initialDirectory);
			if (path.isDirectory()) return path;
		}
		
		return null;
	}
	
	private void saveSavingPath(File file) {
		Components.getSettings().setProperty(AppProperties.SAVE_PATH, file.getParent());
		Components.getSettings().saveToFile();
	};
}
