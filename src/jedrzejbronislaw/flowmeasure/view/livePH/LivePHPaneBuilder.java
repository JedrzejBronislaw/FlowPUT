package jedrzejbronislaw.flowmeasure.view.livePH;

import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LivePHPaneBuilder extends Builder<LivePHPaneController> {

	@Getter private String fxmlFilePath = "LivePHPane.fxml";

	@NonNull private final ViewMediator viewMediator;
	@NonNull private final FlowConverters flowConverters;
	@NonNull private final Settings settings;
	
	
	@Override
	protected void afterBuild() {
		controller.setViewMediator(viewMediator);
	}
}
