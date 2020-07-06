package jedrzejbronislaw.flowmeasure;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jedrzejbronislaw.flowmeasure.events.EventType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlowConverter1 implements FlowConverter{

	@NonNull
	private Settings settings;

	private LocalDateTime lastTime = null;
	private LocalDateTime oldTime = null;

	public FlowConverter1(float pulsePerLitre) {
		settings = new Settings();
		settings.setPulsePerLitre(pulsePerLitre);
	}
	
	@Override
	public float pulsesToLitre(int pulses) {
		return pulses/factor();
	}
	
	@Override
	public Float pulsesToLitrePerSec(int pulses) {
		if(oldTime != null && lastTime != null) {
			float interval = ChronoUnit.MILLIS.between(oldTime, lastTime) / 1000f;
			return pulsesToLitrePerSec(pulses, interval);
		} else
			return null;
	}

	@Override
	public float pulsesToLitrePerSec(int pulses, float interval) {
		return pulses/factor()/interval;
	}

	@Override
	public void newDataEvent() {
		oldTime = lastTime;
		lastTime = LocalDateTime.now();
	}

	@Override
	public void newDataEvent(LocalDateTime time) {
		oldTime = lastTime;
		lastTime = time;
	}

	private float factor() {
		return settings.getPulsePerLitre();
	}
	
	@Override
	public void event(EventType event) {
		if (event == EventType.ReceivedData) newDataEvent();
	}
}
