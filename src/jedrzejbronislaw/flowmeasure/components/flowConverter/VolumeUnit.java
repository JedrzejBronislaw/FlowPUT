package jedrzejbronislaw.flowmeasure.components.flowConverter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum VolumeUnit {

	LITER(      "l", 1),
	MILLILITER("ml", 1000);
	
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
