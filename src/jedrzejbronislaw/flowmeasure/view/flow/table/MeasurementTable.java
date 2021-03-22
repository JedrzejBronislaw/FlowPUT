package jedrzejbronislaw.flowmeasure.view.flow.table;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;

public class MeasurementTable extends VBox implements Initializable {

	@FXML private TableView<FlowMeasurement> table;
	@FXML private Button refreshButton;
	
	private TableUpdater tableUpdater;
	
	private Supplier<ProcessRepository> currentProcess;

	
	public MeasurementTable() {
		MyFXMLLoader2.create("MeasurementTable.fxml", this);
		
		tableUpdater = new TableUpdater(table);
		
		Components.getComponentsLoader().addLoadMethod(() -> {
			currentProcess = Components.getRepository()::getCurrentProcessRepository;
			tableUpdater.creatingColumns(currentProcess.get());
		});
	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		refreshButton.setOnAction(e -> this.refreshTable());
	}
	
	protected void refreshTable() {
		tableUpdater.refreshTable(currentProcess.get());
	}
}
