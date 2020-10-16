package jedrzejbronislaw.flowmeasure.components.flowConverter;

import java.time.LocalDateTime;

import jedrzejbronislaw.flowmeasure.events.EventListener;

public interface FlowConverter extends EventListener {
	void setUnit(FlowUnit unit);
	void setUnit(VolumeUnit unit);
	FlowUnit getFlowUnit();
	VolumeUnit getVolumeUnit();

	float toVolume(int pulses);
	Float toFlow(  int pulses);
	float toFlow(  int pulses, float interval);

	float toVolume(int pulses, VolumeUnit unit);
	Float toFlow(  int pulses, FlowUnit unit);
	float toFlow(  int pulses, float interval, FlowUnit unit);

	void newDataEvent();
	void newDataEvent(LocalDateTime time);
}
