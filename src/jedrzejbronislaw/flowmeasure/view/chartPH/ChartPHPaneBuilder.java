package jedrzejbronislaw.flowmeasure.view.chartPH;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartPHPaneBuilder extends Builder<ChartPHPaneController> {

	@Getter private String fxmlFilePath = "ChartPHPane.fxml";
	
	@NonNull private final Supplier<ProcessRepository> currentProcess;
	@NonNull private final FlowConverters flowconverters;
	@NonNull private final Settings settings;
	

	@Override
	protected void afterBuild() {
		ChartPHRefresher chartRefresher = new ChartPHRefresher(flowconverters, controller.getChart(), settings);
		controller.setRefreshButtonAction(options -> chartRefresher.refresh(options, currentProcess.get()));
	}
}
