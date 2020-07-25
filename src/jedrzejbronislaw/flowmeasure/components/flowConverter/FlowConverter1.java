package jedrzejbronislaw.flowmeasure.components.flowConverter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;
import lombok.NonNull;

public class FlowConverter1 implements FlowConverter{

	@NonNull
	private final Settings settings;
	private final PropertyDesc ratioProperty;

	private LocalDateTime lastTime = null;
	private LocalDateTime oldTime = null;

	
	public FlowConverter1(Settings settings, int flowmeter) {
		this.settings = settings;
		ratioProperty = new RatioProperty(flowmeter);
	}
	
	public FlowConverter1(float pulsePerLitre) {
		ratioProperty = new RatioProperty(1);
		settings = new Settings();
		settings.setProperty(ratioProperty, pulsePerLitre);
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
		return settings.getFloat(ratioProperty);
	}
	
	@Override
	public void event(EventType event) {
		if (event == EventType.ReceivedData) newDataEvent();
	}
}
