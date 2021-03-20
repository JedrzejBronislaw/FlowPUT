package jedrzejbronislaw.flowmeasure.components;

import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsService {
	
	@NonNull private final Settings settings;
	

	public String[] getFlowmeterNames() {
		String[] names = new String[Consts.FLOWMETERS_NUMBER];
		
		for(int i = 0; i < Consts.FLOWMETERS_NUMBER; i++)
			names[i] = settings.getString(new FlowmeterNameProperty(i));

		return names;
	}
}
