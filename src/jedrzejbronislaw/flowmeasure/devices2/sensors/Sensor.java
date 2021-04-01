package jedrzejbronislaw.flowmeasure.devices2.sensors;

import jedrzejbronislaw.flowmeasure.devices2.DataFlow;
import jedrzejbronislaw.flowmeasure.devices2.converters.ValueConverter;

public abstract class Sensor {
	
	protected ValueConverter converter;
	protected DataFlow dataFlow;
	
	public abstract String getTypeName();
//	public abstract String getName();

	public Sensor(ValueConverter converter) {
		this.converter = converter;
	}

	public void setFlow(DataFlow dataFlow) {
		this.dataFlow = dataFlow;
	}
	
	public float receiveData(String data) {
		float value = converter.convert(data);
		System.out.println(getTypeName() + " received data: " + data + " -> " + value);
		
		return value;
	}
}
