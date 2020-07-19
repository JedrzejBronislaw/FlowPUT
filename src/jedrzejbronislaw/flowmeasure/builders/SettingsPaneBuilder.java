package jedrzejbronislaw.flowmeasure.builders;

import javafx.scene.layout.Pane;
import jedrzejbronislaw.flowmeasure.controllers.PulseRatioSettingsPaneController;
import jedrzejbronislaw.flowmeasure.controllers.SettingsPaneController;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsPaneBuilder extends Builder<SettingsPaneController> {

	@Getter private String fxmlFilePath = "SettingsPane.fxml";

	private final Settings settings;
	private boolean activeUpdating = true;
	
	private PulseRatioSettingsPaneController ratio;
	
	@Override
	void afterBuild() {
		
		addRatioPanes(0);
		
		controller.setSettings(settings);
		
		controller.setSavingAction(() -> {
			
			activeUpdating = false;
			setSettings();
			activeUpdating = true;
			
			settings.saveToFile();
		});
		
		settings.addChangeListiner(() -> {
			if (!activeUpdating ) return;

			controller.setSettings(settings);
			ratio.setValue(settings.getFloat(AppProperties.PULSE_PER_LITRE));
		});
	}

	private void addRatioPanes(int position) {
		Pane pane = (Pane) node;
		
		PulseRatioSettingsPaneBuilder ratioPaneBuilder = new PulseRatioSettingsPaneBuilder("Pulses per litre :");
		ratioPaneBuilder.build();
		ratio = ratioPaneBuilder.getController();
		
		pane.getChildren().add(position, ratioPaneBuilder.node);
	}

	private void setSettings() {
		float pulsesPerLitre = ratio.getValue();
		boolean isBuffer     = controller.isSelectedBuffer();
		int bufferSize       = controller.getBufferSize();
		
		try {
			settings.setProperty(AppProperties.PULSE_PER_LITRE, pulsesPerLitre);
		} catch (NumberFormatException e) {}
		
		settings.setProperty(AppProperties.BUFFERED_DATA, isBuffer);
		
		try {
			settings.setProperty(AppProperties.BUFFER_INTERVAL, bufferSize);
		} catch (NumberFormatException e) {}
	}
}
