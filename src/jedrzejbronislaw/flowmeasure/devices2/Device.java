package jedrzejbronislaw.flowmeasure.devices2;

import java.util.Arrays;
import java.util.List;

import jedrzejbronislaw.flowmeasure.devices2.sensors.Sensor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Device {
	
	@Getter private final String name;
	        private final List<Sensor> sensors;
	
	        
	public void setFlow(DataFlow flow) {
		sensors.forEach(s -> s.setFlow(flow));
	}
	
	public void receiveData(String[] data) {
		System.out.println("Received data: " + Arrays.toString(data));
		float[] values = convertData(data);
		System.out.println("Received values: " + Arrays.toString(values));
	}
	
	public float[] convertData(String[] data) {
		float[] values = new float[sensors.size()];

		int i = 0;
		for (Sensor sensor : sensors)
			values[i] = sensor.receiveData(data[i++]);
		
		return values;
	}
}
