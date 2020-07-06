package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class MeasurementTableController implements Initializable{

	@FXML @Getter
	private TableView<FlowMeasurement> table;
	@FXML
	private Button refreshButton;
	
	@Setter
	private Consumer<TableView<FlowMeasurement>> refreshButtonAction;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		refreshButton.setOnAction(e -> Injection.run(refreshButtonAction, table));
	}
}
