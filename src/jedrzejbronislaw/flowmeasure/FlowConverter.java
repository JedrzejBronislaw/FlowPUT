package jedrzejbronislaw.flowmeasure;

import java.time.LocalDateTime;

public interface FlowConverter {
	String FLOW_UNIT = "l/s";
	String VOLUME_UNIT = "l";
	
	float pulsesToLitre(int pulses);
	Float pulsesToLitrePerSec(int pulses);

	void newDataEvent();
	void newDataEvent(LocalDateTime time);
	float pulsesToLitrePerSec(int pulses, float interval);

}
