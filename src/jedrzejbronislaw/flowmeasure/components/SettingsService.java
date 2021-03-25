package jedrzejbronislaw.flowmeasure.components;

import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;

public class SettingsService {
	
	private Settings settings;
	
	
	public SettingsService() {
		Components.getComponentsLoader().addLoadMethod(() -> {
			settings = Components.getSettings();
		});
	}
	
	
	public String[] getFlowmeterNames() {
		String[] names = new String[Consts.FLOWMETERS_NUMBER];
		
		for (int i = 0; i < Consts.FLOWMETERS_NUMBER; i++)
			names[i] = settings.getString(new FlowmeterNameProperty(i));

		return names;
	}
}
