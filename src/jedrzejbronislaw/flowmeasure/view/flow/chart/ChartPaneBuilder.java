package jedrzejbronislaw.flowmeasure.view.flow.chart;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartPaneBuilder extends Builder<ChartPaneController> {

	@Getter private String fxmlFilePath = "ChartPane.fxml";
	
	@NonNull private final Supplier<ProcessRepository> currentProcess;
	@NonNull private final FlowConverters flowconverters;
	@NonNull private final Settings settings;
	

	@Override
	protected void afterBuild() {
		ChartRefresher chartRefresher = new ChartRefresher(flowconverters, controller.getChart(), settings);
		controller.setRefreshButtonAction(options -> chartRefresher.refresh(options, currentProcess.get()));
	}
}
