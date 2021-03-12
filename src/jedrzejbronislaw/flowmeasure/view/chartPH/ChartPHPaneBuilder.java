package jedrzejbronislaw.flowmeasure.view.chartPH;

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
public class ChartPHPaneBuilder extends Builder<ChartPHPaneController> {

	@Getter private String fxmlFilePath = "ChartPHPane.fxml";
	
	@NonNull private final Supplier<ProcessRepository> currentProcess;
	@NonNull private final FlowConverters flowconverters;
	@NonNull private final Settings settings;
	

	@Override
	protected void afterBuild() {
		ChartPHRefresher chartPHRefresher = new ChartPHRefresher(flowconverters, controller.getChartPH(), settings);
		ChartPHRefresher chartECRefresher = new ChartPHRefresher(flowconverters, controller.getChartEC(), settings);
		ChartPHRefresher chartAMRefresher = new ChartPHRefresher(flowconverters, controller.getChartAM(), settings);
		
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
