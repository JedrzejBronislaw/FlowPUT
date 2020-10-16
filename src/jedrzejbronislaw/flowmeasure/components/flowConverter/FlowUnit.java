package jedrzejbronislaw.flowmeasure.components.flowConverter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FlowUnit {

	LITRE_PER_SECOND("l/s", 1),
	LITRE_PER_HOUR(  "l/h", 60*60);
	
	
	private final String name;
	private final float ratio;
	
	public float getValue(float litrePerSec) {
		return litrePerSec * ratio;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
