package jedrzejbronislaw.flowmeasure.builders;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.controllers.MeasurementTableController;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeasurementTableBuilder extends Builder<MeasurementTableController> {

	@Getter private String fxmlFilePath = "MeasurementTable.fxml";
	
	@NonNull
	private final Supplier<ProcessRepository> currentProcess;

	@Override
	void afterBuild() {
		TableUpdater tableUpdater = new TableUpdater(controller.getTable());

		tableUpdater.creatingColumns(currentProcess.get());
		controller.setRefreshButtonAction(() -> tableUpdater.refreshTable(currentProcess.get()));
	}
}
