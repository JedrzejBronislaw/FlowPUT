package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.controllers.SettingsPaneController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsPaneBuilder extends Builder<SettingsPaneController> {

	@Getter private String fxmlFilePath = "SettingsPane.fxml";

	private final Settings settings;
	
	@Override
	void afterBuild() {
		
		controller.setSettings(settings);
		
		controller.setSavingAction(() -> {
			try {
				settings.setPulsePerLitre(controller.getPulsesPerLitre());
			} catch (NumberFormatException e) {
				
			}
			settings.setBufferedData(controller.isSelectedBuffer());
			try {
				settings.setBufferInterval(controller.getBufferSize());
			} catch (NumberFormatException e) {
				
			}
			
			settings.write();
		});
		
		settings.addChangeListiner(() -> controller.setSettings(settings));
	}
}
