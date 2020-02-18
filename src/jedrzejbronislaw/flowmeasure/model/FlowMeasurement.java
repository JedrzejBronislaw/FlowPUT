package jedrzejbronislaw.flowmeasure.model;

import java.time.LocalDateTime;

import lombok.Getter;

public class FlowMeasurement {

	@Getter
	private LocalDateTime time = null;
	private int values[];

	public FlowMeasurement(int size) {
		values = new int[size];
	}
	
	public FlowMeasurement(int[] pulses) {
		time = LocalDateTime.now();
		int size = pulses.length;
		
		values = new int[size];

		for(int i=0; i<size; i++)
			values[i] = pulses[i];
	}
	
	public FlowMeasurement(LocalDateTime time, int[] pulses) {
		this.time = time;
		int size = pulses.length;
		
		values = new int[size];

		for(int i=0; i<size; i++)
			values[i] = pulses[i];
	}
	
	public void setPulsesAndTime(int nr, int pulses) {
		values[nr] = pulses;
		time = LocalDateTime.now();
	}

	public int get(int nr) {
		return values[nr];
	}
}
