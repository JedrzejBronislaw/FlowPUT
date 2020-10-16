package jedrzejbronislaw.flowmeasure.components.flowConverter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum VolumeUnit {
	
	LITRE("l", 1);
	
	private final String name;
	private final float ratio;
	
	public float getValue(float litre) {
		return litre * ratio;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
