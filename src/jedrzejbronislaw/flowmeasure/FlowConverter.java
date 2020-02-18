package jedrzejbronislaw.flowmeasure;

import java.time.LocalDateTime;

public interface FlowConverter {

	float pulsesToLitre(int pulses);
	Float pulsesToLitrePerSec(int pulses);

	void newDataEvent();
	void newDataEvent(LocalDateTime time);
	String getFlowUnit();
	String getVolumeUnit();
	float pulsesToLitrePerSec(int pulses, float interval);

}
