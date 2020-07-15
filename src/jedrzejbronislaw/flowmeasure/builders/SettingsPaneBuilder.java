package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.controllers.SettingsPaneController;
import jedrzejbronislaw.flowmeasure.settings.Settings;
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
			
			controller.suspendUpdating();
			setSettings();
			controller.resumeUpdating();
			
			settings.write();
		});
		
		settings.addChangeListiner(() -> controller.setSettings(settings));
	}

	private void setSettings() {
		float pulsesPerLitre = controller.getPulsesPerLitre();
		boolean isBuffer     = controller.isSelectedBuffer();
		int bufferSize       = controller.getBufferSize();
		
		try {
			settings.setPulsePerLitre(pulsesPerLitre);
		} catch (NumberFormatException e) {}
		
		settings.setBufferedData(isBuffer);
		
		try {
			settings.setBufferInterval(bufferSize);
		} catch (NumberFormatException e) {}
	}
}
