package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.services.EventListener;
import lombok.Getter;

public class MainWindowController implements Initializable {


	
	@FXML
	private VBox mainVbox;
	
	@FXML
	private VBox flowBox;
	
	@FXML
	@Getter
	private BorderPane borderPane;
	
	@FXML
	private ScrollPane tablePane, chartPane, optionsPane, calibrationPane;
	
//	@FXML
//	private Label flow1,flow2,flow3,flow4,flow5,flow6;

//	public void addToMainVBox(Node node) {//TODO delete
//		mainVbox.getChildren().add(node);
//	}
	
	public void addFlowPreview(Node node) {
		flowBox.getChildren().add(node);
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
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}


	
	
}
