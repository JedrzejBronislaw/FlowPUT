package jedrzejbronislaw.flowmeasure.view.flow.table;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;
import lombok.NonNull;

public class MeasurementTable extends VBox implements Initializable {

	@FXML private TableView<FlowMeasurement> table;
	@FXML private Button refreshButton;
	
	private TableUpdater tableUpdater;

	
	@NonNull private final Supplier<ProcessRepository> currentProcess;

	
	public MeasurementTable(Supplier<ProcessRepository> currentProcess) {
		this.currentProcess = currentProcess;
		
		MyFXMLLoader2.create("MeasurementTable.fxml", this);
		
		tableUpdater = new TableUpdater(table);
		tableUpdater.creatingColumns(currentProcess.get());
	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		refreshButton.setOnAction(e -> this.refreshTable());
	}
	
	protected void refreshTable() {
		tableUpdater.refreshTable(currentProcess.get());
	}
}
