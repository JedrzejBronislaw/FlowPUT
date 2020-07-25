package jedrzejbronislaw.flowmeasure.components.dataBuffer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.NonNull;

public class DataBuffer1 implements DataBuffer {

	@NonNull
	private ProcessRepository repository;
	
	@Getter
	private int interval;
	
	@Getter
	private int size;
	@Getter
	private int[] buffer;
	private LocalDateTime lastTime = null;
	
	public DataBuffer1(ProcessRepository repository, int interval) {
		this.repository = repository;
		this.interval = interval;
		
		size = repository.getNumOfFlowmeters();
		buffer = new int[size];
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		newFlows(pulses);
	}
	
	@Override
	public void newFlows(int[] flows) {
		newFlow(LocalDateTime.now(), flows);
	}

	@Override
	public void newFlow(int flow) {
		newFlow(LocalDateTime.now(), flow);
	}
	
	public void newFlow(LocalDateTime time, int flow) {
		newFlow(time, new int[] {flow});
	}

	public void newFlow(LocalDateTime time, int[] flows) {
		if (firstTime(time)) return;
		addToBuffer(flows);
		out(time);
	}

	private void addToBuffer(int[] flows) {
		int length = Math.min(flows.length, size);
		
		for(int i=0; i<length; i++)
			buffer[i] += flows[i];
	}

	private boolean firstTime(LocalDateTime time) {
		if (lastTime == null) {
			lastTime = time;
			return true;
		}
		
		return false;
	}

	private void out(LocalDateTime now) {
		long period;
		
		while((period = sinceLastTime(now)) >= interval)
			saveToRepository(updateTime(), takePortion(period));
	}

	private LocalDateTime updateTime() {
		lastTime = lastTime.plus(interval, ChronoUnit.MILLIS);
		return lastTime;
	}

	private long sinceLastTime(LocalDateTime now) {
		return ChronoUnit.MILLIS.between(lastTime, now);
	}

	private int[] takePortion(long sinceLastTime) {
		int[] portion = new int[size];
		float ratio = (float)interval/sinceLastTime;
		
		for(int i=0; i<size; i++) {
			portion[i] = Math.round(buffer[i]*ratio);
			buffer[i] -= portion[i];
		}
		
		return portion;
	}
	
	public void saveToRepository(LocalDateTime time, int[] flows) {
		repository.addFlowMeasurement(time, flows);
	}
}
