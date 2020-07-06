package jedrzejbronislaw.flowmeasure.builders;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartPaneBuilder extends Builder<ChartPaneController> {

	@Getter private String fxmlFilePath = "ChartPane.fxml";
	
	private final Supplier<ProcessRepository> currentProcess;
	private final FlowConverter flowconverter;
	

	@Override
	void afterBuild() {
		controller.setRefreshButtonAction(new ChartRefresher(currentProcess, flowconverter, controller));
	}
}
