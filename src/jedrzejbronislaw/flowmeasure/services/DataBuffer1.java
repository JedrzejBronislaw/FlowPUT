package jedrzejbronislaw.flowmeasure.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class DataBuffer1 implements DataBuffer {

	@NonNull
	private ProcessRepository repository;
	
	@Setter @Getter
	private int interval;
	
	@Getter
	private int buffer = 0;
	private LocalDateTime lastTime = null;
	
	public DataBuffer1(ProcessRepository repository, int interval) {
		this.repository = repository;
		this.interval = interval;
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		newFlows(pulses);
	}
	
	@Override
	public void newFlows(int[] flows) {
		newFlow(flows[0]);
	}

	@Override
	public void newFlow(int flow) {
		newFlow(LocalDateTime.now(), flow);
	}
	
	public void newFlow(LocalDateTime time, int flow) {
		if (firstTime(time)) return;
		buffer += flow;
		out(time);
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

	private int takePortion(long sinceLastTime) {
		float ratio = (float)interval/sinceLastTime;
		
		int portion = Math.round(buffer*ratio);
		buffer -= portion;
		
		return portion;
	}
	
	public void saveToRepository(LocalDateTime time, int flow) {
		int[] tempFlows = new int[6];

		tempFlows[0] = flow;
		repository.addFlowMeasurement(time, tempFlows);
	}
}
