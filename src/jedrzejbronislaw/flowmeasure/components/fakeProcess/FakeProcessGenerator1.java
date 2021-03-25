package jedrzejbronislaw.flowmeasure.components.fakeProcess;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public class FakeProcessGenerator1 implements FakeProcessGenerator {

	private int numOfFlowmeters = 0;
	private int interval = 0;
	private LocalDateTime time;
	
	@Override
	public FakeProcessGenerator setNumOfFlowmeters(int n) {
		numOfFlowmeters = n;
		return this;
	}

	@Override
	public FakeProcessGenerator setInterval(int interval) {
		this.interval = interval;
		return this;
	}

	@Override
	public void generate(ProcessRepository model, long size) {
		if (numOfFlowmeters <=0 || interval <= 0)
			throw new IllegalStateException();

		int[] pulses = new int[numOfFlowmeters];
		time = LocalDateTime.of(2000, 1, 1, 0, 0);
		
		model.getMetadata().setStartTime(time);
		for (int i=0; i<size; i++) {
			model.addFlowMeasurement(time, pulses);
			time = time.plus(interval, ChronoUnit.MILLIS);
		}
		time = time.minus(interval, ChronoUnit.MILLIS);
		model.getMetadata().setEndTime(time);
	}
}
