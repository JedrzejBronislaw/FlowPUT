package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.controllers.PulseRatioSettingsPaneController;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PulseRatioSettingsPaneBuilder extends Builder<PulseRatioSettingsPaneController> {

	@Getter private String fxmlFilePath = "PulseRatioSettingsPane.fxml";

	@NonNull private String name;
	
	@Override
	void afterBuild() {
		controller.setName(name);
	}
}
