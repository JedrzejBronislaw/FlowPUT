package jedrzejbronislaw.flowmeasure.devices2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.devices2.sensors.Sensor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Device {
	
	@Getter private final String name;
	        private final List<Sensor> sensors;
	        
	private Map<Sensor, Consumer<Float>> sensorOutputs = new HashMap<>();
	
	        
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
		for (Sensor sensor : sensors) {
			values[i] = sensor.receiveData(data[i]);
			output(sensor, values[i]);
			i++;
		}
		
		return values;
	}
	
	private void output(Sensor sensor, float value) {
		Consumer<Float> output = sensorOutputs.get(sensor);
		if (output == null) return;
		
		output.accept(value);
	}

	public void setSensorOutput(int sensorNumber, Consumer<Float> output) {
		if (output == null) return;
		if (sensorNumber < 0 || sensorNumber >= sensors.size()) return;
		
		Sensor sensor = sensors.get(sensorNumber);
		sensorOutputs.put(sensor, output);
	}
}
