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
	
	private int pulseBuffer = 0;
	private LocalDateTime lastTime = null;
	
	public DataBuffer1(ProcessRepository repository, int interval) {
		this.repository = repository;
		this.interval = interval;
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

		if (lastTime == null) {
			firstTime(time);
			return;
		}
		
		System.out.println(
				lastTime.toString() + "\t" +
				flow + "\t" +
				pulseBuffer + "\t" +
				LocalDateTime.now());
		
		pulseBuffer += flow;

		out(time);
	}

	private void firstTime(LocalDateTime now) {
		lastTime = now;
		System.out.println("now:   " + now.toString());
		System.out.println("start: " + repository.getMetadata().getStartTime());
	}

	private void out(LocalDateTime now) {
		long period;
		int portion;
		
		while((period = sinceLastTime(now)) >= interval) {
			portion = takePortion(period);
			
			updateLastTime();
			saveToRepository(LocalDateTime.from(lastTime), portion);
		}
	}

	private void updateLastTime() {
		lastTime = lastTime.plus(interval, ChronoUnit.MILLIS);
	}

	private long sinceLastTime(LocalDateTime now) {
		return ChronoUnit.MILLIS.between(lastTime, now);
	}

	private int takePortion(long sinceLastTime) {
		float ratio = (float)interval/sinceLastTime;
		
		int portion = Math.round(pulseBuffer*ratio);
		pulseBuffer -= portion;
		
		return portion;
	}
	
	public void saveToRepository(LocalDateTime time, int flow) {
		int[] tempFlows = new int[6];
		
		System.out.println("  " +
			time.toString() + "\t" +
			flow + "\t" +
			(ChronoUnit.MILLIS.between(repository.getMetadata().getStartTime(), time)) + "\t" +
			(ChronoUnit.MILLIS.between(repository.getMetadata().getStartTime(), time)/1000));
		
		tempFlows[0] = flow;
		repository.addFlowMeasurement(time, tempFlows);
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		newFlows(pulses);
	}
}
