package jedrzejbronislaw.flowmeasure;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
		return pulses/settings.getPulsePerLitre();
	}
	
	@Override
	public Float pulsesToLitrePerSec(int pulses) {
		if(oldTime != null && lastTime != null) {
			float interval = (ChronoUnit.MILLIS.between(oldTime, lastTime)/1000f);
			return pulses/settings.getPulsePerLitre()/interval;
		} else
			return null;
	}

	@Override
	public float pulsesToLitrePerSec(int pulses, float interval) {
		return pulses/settings.getPulsePerLitre()/interval;
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

	@Override
	public String getFlowUnit() {
		return "l/s";
	}

	@Override
	public String getVolumeUnit() {
		return "l";
	}
}
