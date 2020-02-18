package jedrzejbronislaw.flowmeasure.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class DataBuffer1 implements DataBuffer {

	@NonNull
	private ProcessRepository repository;
	
	@NonNull
	@Setter
	@Getter
	private int interval;
	
	private int pulseBuffer;
	
	private LocalDateTime lastTime = null;
	
	@Override
	public void newFlows(int[] flows) {
		
		newFlow(flows[0]);
		
	}

	@Override
	public void newFlow(int flow) {
		LocalDateTime now = LocalDateTime.now();
		long period;

		if (lastTime == null) {
			lastTime = now;
			System.out.println("now:   " + now.toString());
			System.out.println("start: " + repository.getMetadata().getStartTime());
			return;
		}
		
		System.out.println(
				lastTime.toString() + "\t" +
				flow + "\t" +
				pulseBuffer + "\t" +
				LocalDateTime.now());
		
		pulseBuffer += flow;

		do {
//			System.out.println("1");
			period = ChronoUnit.MILLIS.between(lastTime, now);	

//			System.out.println("2");
			if(period >= interval) {
//				System.out.println("3");
				float ratio = (float)interval/period;
				
				int portion = Math.round(pulseBuffer*ratio);
				pulseBuffer -= portion;
				
				lastTime = lastTime.plus(interval, ChronoUnit.MILLIS);
				out(LocalDateTime.from(lastTime), portion);
			}
			
		} while(period >= interval);
	}
	
	public void out(LocalDateTime time, int flow) {
		int[] tempFlows = new int[6];
		
		tempFlows[0] = flow;
		
			System.out.println("  " +
						time.toString() + "\t" +
						flow + "\t" +
						( ChronoUnit.MILLIS.between(repository.getMetadata().getStartTime(), time)) + "\t" +
						( ChronoUnit.MILLIS.between(repository.getMetadata().getStartTime(), time)/1000));
		repository.addFlowMeasurement(time, tempFlows);
		
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		newFlows(pulses);
	}
}
