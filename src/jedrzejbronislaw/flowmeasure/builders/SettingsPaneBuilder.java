package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.controllers.SettingsPaneController;
import jedrzejbronislaw.flowmeasure.settings.PropertyName;
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
			
			settings.saveToFile();
		});
		
		settings.addChangeListiner(() -> controller.setSettings(settings));
	}

	private void setSettings() {
		float pulsesPerLitre = controller.getPulsesPerLitre();
		boolean isBuffer     = controller.isSelectedBuffer();
		int bufferSize       = controller.getBufferSize();
		
		try {
			settings.setProperty(PropertyName.PULSE_PER_LITRE, pulsesPerLitre);
		} catch (NumberFormatException e) {}
		
		settings.setProperty(PropertyName.BUFFERED_DATA, isBuffer);
		
		try {
			settings.setProperty(PropertyName.BUFFER_INTERVAL, bufferSize);
		} catch (NumberFormatException e) {}
	}
}
