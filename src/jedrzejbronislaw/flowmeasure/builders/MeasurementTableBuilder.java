package jedrzejbronislaw.flowmeasure.builders;

import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import jedrzejbronislaw.flowmeasure.controllers.MeasurementTableController;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeasurementTableBuilder extends Builder<MeasurementTableController> {

	@Getter private String fxmlFilePath = "MeasurementTable.fxml";
	
	private final Supplier<ProcessRepository> currentProcess;

	@Override
	void afterBuild() {
		controller.setRefreshButtonAction(table -> {
			Platform.runLater(() -> {
			int size = currentProcess.get().getNumOfFlowmeters();

			table.getItems().clear();
			table.getColumns().clear();
			
			TableColumn<FlowMeasurement, String> columnT;
			columnT = new TableColumn<>();
//			DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		    
			columnT.setText("Time");
			columnT.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FlowMeasurement,String>, ObservableValue<String>>() {
				
				@Override
				public ObservableValue<String> call(CellDataFeatures<FlowMeasurement, String> fm) {
					return new SimpleStringProperty(fm.getValue().getTime().format(timeFormat));
				}
			});
			
			table.getColumns().add(columnT);
			
			TableColumn<FlowMeasurement, Integer> column;
			for(int i=0; i<size; i++) {
				column = new TableColumn<>();
		        
				column.setText("Flow " + (i+1));
				int ii = i;
				column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FlowMeasurement,Integer>, ObservableValue<Integer>>() {
					
					@Override
					public ObservableValue<Integer> call(CellDataFeatures<FlowMeasurement, Integer> fm) {

						return new SimpleIntegerProperty(fm.getValue().get(ii)).asObject();
					}
				});
				
				table.getColumns().add(column);
			}
			
			table.getItems().addAll(currentProcess.get().getAllMeasurement());
			table.scrollTo(table.getItems().size()-1);
		});
		});
	}
}
