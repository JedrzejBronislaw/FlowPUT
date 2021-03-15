package jedrzejbronislaw.flowmeasure.view.chartED;

import java.util.Arrays;
import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartEDPaneBuilder extends Builder<ChartEDPaneController> {

	@Getter private String fxmlFilePath = "ChartEDPane.fxml";
	
	@NonNull private final Supplier<ProcessRepository> currentProcess;
	@NonNull private final FlowConverters flowconverters;
	@NonNull private final Settings settings;
	

	@Override
	protected void afterBuild() {
		ChartEDRefresher chartPHRefresher = new ChartEDRefresher(flowconverters, controller.getChartPH(), settings);
		ChartEDRefresher chartECRefresher = new ChartEDRefresher(flowconverters, controller.getChartEC(), settings);
		ChartEDRefresher chartAMRefresher = new ChartEDRefresher(flowconverters, controller.getChartAM(), settings);
		
		chartPHRefresher.setSeriesFilter(Arrays.asList(0));
		chartECRefresher.setSeriesFilter(Arrays.asList(1));
		chartAMRefresher.setSeriesFilter(Arrays.asList(2));
		
		controller.setRefreshButtonAction(options -> {
			chartPHRefresher.refresh(options, currentProcess.get());
			chartECRefresher.refresh(options, currentProcess.get());
			chartAMRefresher.refresh(options, currentProcess.get());
		});
	}
}
