package jedrzejbronislaw.flowmeasure.devices2.sensors;

import jedrzejbronislaw.flowmeasure.devices2.DataFlow;
import jedrzejbronislaw.flowmeasure.devices2.ValueConverter;

public abstract class Sensor {
	
	protected ValueConverter converter;
	protected DataFlow dataFlow;
	
	public abstract String getTypeName();
//	public abstract String getName();


	public void setFlow(DataFlow dataFlow) {
		this.dataFlow = dataFlow;
	}
	
	public void receiveData(String data) {
		System.out.println(getTypeName() + " received data: " + data);
	}
}
