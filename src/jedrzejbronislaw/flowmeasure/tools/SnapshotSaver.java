package jedrzejbronislaw.flowmeasure.tools;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

public class SnapshotSaver {
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
	
	public static void withFileChooser(Node node) {
		WritableImage wImage = node.snapshot(null, null);
		
		File file = selectFile();
		if(file == null) return;
		
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), "png", file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static File selectFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName(LocalDateTime.now().format(FORMATTER) + ".png");
		File file = fileChooser.showSaveDialog(null);
		return file;
	}
}
