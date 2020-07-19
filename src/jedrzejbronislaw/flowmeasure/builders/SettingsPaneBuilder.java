package jedrzejbronislaw.flowmeasure.builders;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.Pane;
import jedrzejbronislaw.flowmeasure.controllers.PulseRatioSettingsPaneController;
import jedrzejbronislaw.flowmeasure.controllers.SettingsPaneController;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsPaneBuilder extends Builder<SettingsPaneController> {

	private static final int FLOWMETERS_NUMBER = 6;
	private static final String PULSES_PER_LITRE_LABEL = "Pulses per litre ";
	
	@Getter private String fxmlFilePath = "SettingsPane.fxml";

	private final Settings settings;
	private boolean activeUpdating = true;
	
	private List<PulseRatioSettingsPaneController> ratios = new ArrayList<>();
	
	@Override
	void afterBuild() {
		
		addRatioPanes(FLOWMETERS_NUMBER, 0);
		
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
			for (int i=0; i<FLOWMETERS_NUMBER; i++)
				ratios.get(i).setValue(settings.getFloat(new RatioProperty(i)));
		});
	}
	
	private void addRatioPanes(int number, int position) {
		Pane pane = (Pane) node;
		
		for(int i=0; i<number; i++) {
			PulseRatioSettingsPaneBuilder ratioPaneBuilder = new PulseRatioSettingsPaneBuilder(PULSES_PER_LITRE_LABEL + (i+1) + ":");
			ratioPaneBuilder.build();
			ratios.add(ratioPaneBuilder.getController());
			
			pane.getChildren().add(i + position, ratioPaneBuilder.node);
		}
	}

	private void setSettings() {
		boolean isBuffer     = controller.isSelectedBuffer();
		int bufferSize       = controller.getBufferSize();
		
		settings.setProperty(AppProperties.BUFFERED_DATA, isBuffer);
		settings.setProperty(AppProperties.BUFFER_INTERVAL, bufferSize);
		
		for (int i=0; i<FLOWMETERS_NUMBER; i++)
			settings.setProperty(new RatioProperty(i), ratios.get(i).getValue());
	}
}
