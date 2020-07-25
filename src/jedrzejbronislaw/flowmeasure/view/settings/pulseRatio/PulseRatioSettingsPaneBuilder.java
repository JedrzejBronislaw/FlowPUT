package jedrzejbronislaw.flowmeasure.view.settings.pulseRatio;

import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PulseRatioSettingsPaneBuilder extends Builder<PulseRatioSettingsPaneController> {

	@Getter private String fxmlFilePath = "PulseRatioSettingsPane.fxml";

	@NonNull private String name;
	
	@Override
	protected void afterBuild() {
		controller.setName(name);
	}
}
