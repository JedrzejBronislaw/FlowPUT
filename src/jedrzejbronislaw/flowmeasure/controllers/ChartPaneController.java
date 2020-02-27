package jedrzejbronislaw.flowmeasure.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import jedrzejbronislaw.flowmeasure.tools.Refresher;
import lombok.Getter;
import lombok.Setter;

public class ChartPaneController implements Initializable{

	public enum ValueUnit{
		Pulses, Litre, LitrePerSec
	}
	
	
	@FXML
	private Button refreshButton, saveButton;

	@FXML
	private BorderPane mainPane;

	@FXML
	private CheckBox lastSecsBox, liveBox;
	
	@FXML
	private RadioButton pulsesRadio, litresRadio, litresPerSecRadio;
	
	
	@Setter
	private Runnable lastSecsBoxAction;

	@Getter
	private boolean lastSeconds;
	
	@Setter
	private Consumer<LineChart<Number, Number>> refreshButtonAction;
	
	@Getter
	private NumberAxis axisX, axisY;
	private LineChart<Number, Number> chart;
	
	private Refresher liveChartRefresher = null;
	
	private void startRefresher() {
		if(liveChartRefresher == null) {
			liveChartRefresher = new Refresher(1000, () ->  {
				if(refreshButtonAction != null)
					refreshButtonAction.accept(chart);
			});
		}
	}
	
	private void stopRefresher() {
		if(liveChartRefresher != null) {
			liveChartRefresher.off();
			liveChartRefresher = null;
		}
		
	}

	public ValueUnit getValueUnit(){
		if(pulsesRadio.isSelected())
			return ValueUnit.Pulses;
		if(litresRadio.isSelected())
			return ValueUnit.Litre;
		if(litresPerSecRadio.isSelected())
			return ValueUnit.LitrePerSec;
		return null;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		axisX = new NumberAxis();
		axisY = new NumberAxis();
		chart = new LineChart<Number, Number>(axisX, axisY);
//		axisX.setAutoRanging(true);
//		axisY.setfobou setAutoRanging(true);
		axisX.setForceZeroInRange(false);

		
		refreshButton.disableProperty().bind(liveBox.selectedProperty());
		
		mainPane.setCenter(chart);
//		mainVbox.getChildren().add(0, chart);
		
		refreshButton.setOnAction(e -> {
			if(refreshButtonAction != null)
				refreshButtonAction.accept(chart);
		});
		
		saveButton.setOnAction(e -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
			WritableImage wImage = chart.snapshot(null, null);
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(LocalDateTime.now().format(formatter) + ".png");
			File f = fileChooser.showSaveDialog(null);
			if(f == null) return;
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), "png", f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		lastSecsBox.setOnAction(e -> {
			if(lastSecsBoxAction != null)
				lastSecsBoxAction.run();
		});
		
		liveBox.setOnAction(e -> {
			if(liveBox.isSelected())
				startRefresher();
			else
				stopRefresher();
		});
		
		lastSecsBox.setOnAction(e -> lastSeconds = lastSecsBox.isSelected());

	}
	
	
}
