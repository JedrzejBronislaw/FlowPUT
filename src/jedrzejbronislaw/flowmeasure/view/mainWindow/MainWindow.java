package jedrzejbronislaw.flowmeasure.view.mainWindow;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.tools.TextTools;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import lombok.Getter;

public class MainWindow extends StackPane implements Initializable, StateListener<ApplicationState> {

	@Getter
	@FXML private BorderPane borderPane;
	@FXML private VBox mainVbox;
	@FXML private ScrollPane livePane, tablePane, chartPane, optionsPane, calibrationPane;
	@FXML private Label stateLabel;

	
	public MainWindow() {
		MyFXMLLoader.create("MainWindow.fxml", this);
	}

	
	public void setLivePane(Node live) {
		livePane.setContent(live);
	}
	
	public void setTablePane(Node table) {
		tablePane.setContent(table);
	}
	
	public void setChartPane(Node chart) {
		chartPane.setContent(chart);
	}
	
	public void setSettingsPane(Node options) {
		optionsPane.setContent(options);
	}
	
	public void setCalibrationPane(Node options) {
		calibrationPane.setContent(options);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}

	@Override
	public void onChangeState(ApplicationState state) {
		Platform.runLater(() ->
			stateLabel.setText(TextTools.firstCharUpper(state.toString())));
	}
}
