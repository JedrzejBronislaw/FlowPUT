package jedrzejbronislaw.flowmeasure.components.flowConverter;

import java.time.LocalDateTime;

import jedrzejbronislaw.flowmeasure.events.EventListener;

public interface FlowConverter extends EventListener {
	String FLOW_UNIT = "l/s";
	String VOLUME_UNIT = "l";
	
	float pulsesToLitre(int pulses);
	Float pulsesToLitrePerSec(int pulses);

	void newDataEvent();
	void newDataEvent(LocalDateTime time);
	float pulsesToLitrePerSec(int pulses, float interval);
}
