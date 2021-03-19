package jedrzejbronislaw.flowmeasure.view.flow.settings.flowmeterName;

import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlowmeterNameSettingsPaneBuilder extends Builder<FlowmeterNameSettingsPaneController> {

	@Getter private String fxmlFilePath = "FlowmeterNameSettingsPane.fxml";

	@NonNull private String factoryName;
	
	@Override
	protected void afterBuild() {
		controller.setFactoryName(factoryName);
	}
}
