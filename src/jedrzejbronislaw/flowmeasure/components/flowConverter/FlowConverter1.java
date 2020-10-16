package jedrzejbronislaw.flowmeasure.components.flowConverter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;
import lombok.Getter;
import lombok.NonNull;

public class FlowConverter1 implements FlowConverter {

	@NonNull
	private final Settings settings;
	private final PropertyDesc ratioProperty;

	private LocalDateTime lastTime = null;
	private LocalDateTime oldTime = null;

	@Getter private FlowUnit   flowUnit   = FlowUnit.LITER_PER_HOUR;
	@Getter private VolumeUnit volumeUnit = VolumeUnit.LITER;
	
	@Override public void setUnit(FlowUnit unit)   {flowUnit = unit;}
	@Override public void setUnit(VolumeUnit unit) {volumeUnit = unit;}
	
	
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
	public float toVolume(int pulses) {
		return toVolume(pulses, volumeUnit);
	}
	
	@Override
	public float toFlow(int pulses, float interval) {
		return toFlow(pulses, interval, flowUnit);
	}
	
	@Override
	public Float toFlow(int pulses) {
		return toFlow(pulses, flowUnit);
	}
	
	
	@Override
	public float toVolume(int pulses, VolumeUnit volumeUnit) {
		float litre = pulsesToLitre(pulses);
		return volumeUnit.getValue(litre);
	}
	
	@Override
	public float toFlow(int pulses, float interval, FlowUnit flowUnit) {
		float litrePerSec = pulsesToLitrePerSec(pulses, interval);
		return flowUnit.getValue(litrePerSec);
	}
	
	@Override
	public Float toFlow(int pulses, FlowUnit flowUnit) {
		Float litrePerSec = pulsesToLitrePerSec(pulses);
		if (litrePerSec == null) return null;

		return flowUnit.getValue(litrePerSec);
	}
	
	
	private float pulsesToLitre(int pulses) {
		return pulses/factor();
	}

	private float pulsesToLitrePerSec(int pulses, float interval) {
		return pulses/factor()/interval;
	}
	
	private Float pulsesToLitrePerSec(int pulses) {
		if(oldTime == null || lastTime == null) return null;

		float interval = ChronoUnit.MILLIS.between(oldTime, lastTime) / 1000f;
		return pulsesToLitrePerSec(pulses, interval);
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
		if (event == EventType.RECEIVED_DATA) newDataEvent();
	}
}
