package jedrzejbronislaw.flowmeasure.components.flowConverter;

import java.time.LocalDateTime;

import jedrzejbronislaw.flowmeasure.events.EventListener;

public interface FlowConverter extends EventListener {
	float pulsesToLitre(int pulses);
	Float pulsesToLitrePerSec(int pulses);
	Float pulsesToLitrePerHour(int pulses);

	void newDataEvent();
	void newDataEvent(LocalDateTime time);
	float pulsesToLitrePerSec(int pulses, float interval);
	float pulsesToLitrePerHour(int pulses, float interval);
}
