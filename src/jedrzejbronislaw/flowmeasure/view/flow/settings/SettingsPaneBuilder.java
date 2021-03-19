package jedrzejbronislaw.flowmeasure.view.flow.settings;

import java.util.ArrayList;
import java.util.List;

import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import jedrzejbronislaw.flowmeasure.view.flow.settings.flowmeterName.FlowmeterNameSettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.flow.settings.flowmeterName.FlowmeterNameSettingsPaneController;
import jedrzejbronislaw.flowmeasure.view.flow.settings.pulseRatio.PulseRatioSettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.flow.settings.pulseRatio.PulseRatioSettingsPaneController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsPaneBuilder extends Builder<SettingsPaneController> {

	private static final String RATIO_LABEL = "Ratio ";
	private static final String  NAME_LABEL = "Flowmeter ";
	
	@Getter private String fxmlFilePath = "SettingsPane.fxml";

	private final Settings settings;
	private boolean activeUpdating = true;

	private List<PulseRatioSettingsPaneController> ratios = new ArrayList<>();
	private List<FlowmeterNameSettingsPaneController> names = new ArrayList<>();
	
	@Override
	protected void afterBuild() {

		addRatioPanes(Consts.FLOWMETERS_NUMBER);
		addNamePanes( Consts.FLOWMETERS_NUMBER);
		
		controller.setSettings(settings);
		
		controller.setSavingAction(() -> {
			activeUpdating = false;
			setSettings();
			activeUpdating = true;
			
			settings.saveToFile();
		});
		
		settings.addChangeListener(() -> {
			for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
				ratios.get(i).setName(settings.getString(new FlowmeterNameProperty(i)));
			
			if (!activeUpdating ) return;

			controller.setSettings(settings);
			for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++) {
				ratios.get(i).setValue(settings.getFloat(new RatioProperty(i)));
				names.get(i).setName(settings.getString(new FlowmeterNameProperty(i)));
			}
		});
	}
	
	private void addRatioPanes(int number) {
		for(int i=0; i<number; i++) {
			PulseRatioSettingsPaneBuilder ratioPaneBuilder = new PulseRatioSettingsPaneBuilder(RATIO_LABEL + (i+1) + ":");
			ratioPaneBuilder.build();
			ratios.add(ratioPaneBuilder.getController());
			
			controller.addRatioPane(ratioPaneBuilder.getNode());
		}
	}
	
	private void addNamePanes(int number) {
		for(int i=0; i<number; i++) {
			FlowmeterNameSettingsPaneBuilder namePaneBuilder = new FlowmeterNameSettingsPaneBuilder(NAME_LABEL + (i+1) + ":");
			namePaneBuilder.build();
			names.add(namePaneBuilder.getController());
			
			controller.addNamePane(namePaneBuilder.getNode());
		}
	}

	private void setSettings() {
		boolean isBuffer     = controller.isSelectedBuffer();
		int bufferSize       = controller.getBufferSize();
		
		settings.setProperty(AppProperties.BUFFERED_DATA, isBuffer);
		settings.setProperty(AppProperties.BUFFER_INTERVAL, bufferSize);

		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			settings.setProperty(new RatioProperty(i), ratios.get(i).getValue());
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			settings.setProperty(new FlowmeterNameProperty(i), names.get(i).getName());
	}
}
