package jedrzejbronislaw.flowmeasure.view.chart;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartPaneBuilder extends Builder<ChartPaneController> {

	@Getter private String fxmlFilePath = "ChartPane.fxml";
	
	@NonNull private final Supplier<ProcessRepository> currentProcess;
	@NonNull private final FlowConverters flowconverters;
	

	@Override
	protected void afterBuild() {
		ChartRefresher chartRefresher = new ChartRefresher(flowconverters, controller.getChart());
		controller.setRefreshButtonAction(options -> chartRefresher.refresh(options, currentProcess.get()));
	}
}
