package jedrzejbronislaw.flowmeasure.builders;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.FlowConverters;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartPaneBuilder extends Builder<ChartPaneController> {

	@Getter private String fxmlFilePath = "ChartPane.fxml";
	
	@NonNull private final Supplier<ProcessRepository> currentProcess;
	@NonNull private final FlowConverters flowconverters;
	

	@Override
	void afterBuild() {
		ChartRefresher chartRefresher = new ChartRefresher(flowconverters, controller.getChart());
		controller.setRefreshButtonAction(options -> chartRefresher.refresh(options, currentProcess.get()));
	}
}
