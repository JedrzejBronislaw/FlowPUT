package jedrzejbronislaw.flowmeasure.builders;

import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import jedrzejbronislaw.flowmeasure.controllers.MeasurementTableController;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeasurementTableBuilder extends Builder<MeasurementTableController> {

	@Getter private String fxmlFilePath = "MeasurementTable.fxml";
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final String TIME_COLUMN_NAME = "Time";
	private static final String FLOW_COMUMN_NAME_PREFIX = "Flow ";
	
	private final Supplier<ProcessRepository> currentProcess;

	@Override
	void afterBuild() {
		creatingColumns(controller.getTable());
		controller.setRefreshButtonAction(this::refreshTable);
	}
	
	void refreshTable(TableView<FlowMeasurement> table) {
		Platform.runLater(() -> {
			table.getItems().addAll(currentProcess.get().getAllMeasurement());
			table.scrollTo(table.getItems().size()-1);
		});
	}

	private void creatingColumns(TableView<FlowMeasurement> table) {
		int numOfFlowmeters = currentProcess.get().getNumOfFlowmeters();

		table.getItems().clear();
		table.getColumns().clear();
		
		table.getColumns().add(createTimeColumn());
		
		for(int i=0; i<numOfFlowmeters; i++)
			table.getColumns().add(createFlowColumn(i));
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
}
