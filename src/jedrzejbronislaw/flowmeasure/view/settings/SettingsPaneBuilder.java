package jedrzejbronislaw.flowmeasure.view.settings;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.Pane;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import jedrzejbronislaw.flowmeasure.view.settings.pulseRatio.PulseRatioSettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.settings.pulseRatio.PulseRatioSettingsPaneController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsPaneBuilder extends Builder<SettingsPaneController> {

	private static final String PULSES_PER_LITRE_LABEL = "Pulses per litre ";
	
	@Getter private String fxmlFilePath = "SettingsPane.fxml";

	private final Settings settings;
	private boolean activeUpdating = true;
	
	private List<PulseRatioSettingsPaneController> ratios = new ArrayList<>();
	
	@Override
	protected void afterBuild() {
		
		addRatioPanes(Consts.FLOWMETERS_NUMBER, 0);
		
		controller.setSettings(settings);
		
		controller.setSavingAction(() -> {
			activeUpdating = false;
			setSettings();
			activeUpdating = true;
			
			settings.saveToFile();
		});
		
		settings.addChangeListener(() -> {
			if (!activeUpdating ) return;

			controller.setSettings(settings);
			for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
				ratios.get(i).setValue(settings.getFloat(new RatioProperty(i)));
		});
	}
	
	private void addRatioPanes(int number, int position) {
		Pane pane = (Pane) node;
		
		for(int i=0; i<number; i++) {
			PulseRatioSettingsPaneBuilder ratioPaneBuilder = new PulseRatioSettingsPaneBuilder(PULSES_PER_LITRE_LABEL + (i+1) + ":");
			ratioPaneBuilder.build();
			ratios.add(ratioPaneBuilder.getController());
			
			pane.getChildren().add(i + position, ratioPaneBuilder.getNode());
		}
	}

	private void setSettings() {
		boolean isBuffer     = controller.isSelectedBuffer();
		int bufferSize       = controller.getBufferSize();
		
		settings.setProperty(AppProperties.BUFFERED_DATA, isBuffer);
		settings.setProperty(AppProperties.BUFFER_INTERVAL, bufferSize);
		
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			settings.setProperty(new RatioProperty(i), ratios.get(i).getValue());
	}
}
