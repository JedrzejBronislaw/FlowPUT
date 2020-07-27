package jedrzejbronislaw.flowmeasure.view.table;

import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TableUpdater {

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final String TIME_COLUMN_NAME = "Time";
	private static final String FLOW_COMUMN_NAME_PREFIX = "Flow ";
	
	private final TableView<FlowMeasurement> table;
	
	public void refreshTable(ProcessRepository currentProcess) {
		Platform.runLater(() -> {
			if (currentProcess == null) {clear(); return;}
			
			creatingColumns(currentProcess);
			table.getItems().addAll(currentProcess.getAllMeasurement());
			table.scrollTo(table.getItems().size()-1);
		});
	}

	public void creatingColumns(ProcessRepository currentProcess) {
		if (currentProcess == null) return;
		int numOfFlowmeters = currentProcess.getNumOfFlowmeters();

		clear();
		
		addColumn(createTimeColumn());
		for(int i=0; i<numOfFlowmeters; i++) addColumn(createFlowColumn(i));
	}

	private void clear() {
		table.getItems().clear();
		table.getColumns().clear();
	}

	private TableColumn<FlowMeasurement, String> createTimeColumn() {
		TableColumn<FlowMeasurement, String> column = new TableColumn<>();

		column.setText(TIME_COLUMN_NAME);
		column.setCellValueFactory(cell ->
			new SimpleStringProperty(cell.getValue().getTime().format(TIME_FORMATTER))
		);

		return column;
	}

	private TableColumn<FlowMeasurement, Integer> createFlowColumn(int flowNumer) {
		TableColumn<FlowMeasurement, Integer> column = new TableColumn<>();
		
		column.setText(flowColumnName(flowNumer));
		column.setCellValueFactory(fm ->
			new SimpleIntegerProperty(fm.getValue().get(flowNumer)).asObject()
		);
		
		return column;
	}

	private String flowColumnName(int i) {
		return FLOW_COMUMN_NAME_PREFIX + (i+1);
	}
	
	private void addColumn(TableColumn<FlowMeasurement, ?> column) {
		table.getColumns().add(column);
	}
}
