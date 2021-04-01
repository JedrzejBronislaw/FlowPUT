package jedrzejbronislaw.flowmeasure.devices2.converters;

public class NoConverter extends ValueConverter {

	@Override
	public float convert(String data) {
		float value = Integer.parseInt(data);
		value /= 10;
		return value;
	}
}
