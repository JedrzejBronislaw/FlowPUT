package jedrzejbronislaw.flowmeasure.components.flowConverter;

import java.util.stream.Stream;

import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.settings.Settings;

public class FlowConverters implements EventListener {

	private FlowConverter[] converters;
	
	
	public FlowConverters(int number) {
		
		Components.getComponentsLoader().addLoadMethod(() -> {
			create(Components.getSettings(), number);
		});
	}

	public FlowConverter get(int flowmeter) {
		return converters[flowmeter];
	}
	
	private void create(Settings settings, int number) {
		converters = new FlowConverter[number];
		
		for(int i=0; i<number; i++)
			converters[i] = new FlowConverter1(settings, i);
	}

	@Override
	public void event(EventType event) {
		Stream.of(converters).forEach(c -> c.event(event));
	}
}
