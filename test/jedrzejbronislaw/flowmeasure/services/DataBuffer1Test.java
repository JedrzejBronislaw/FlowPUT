package jedrzejbronislaw.flowmeasure.services;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public class DataBuffer1Test {
	
	private ProcessRepository repository = new ProcessRepository(1, "");
	private DataBuffer1 buffer = new DataBuffer1(repository, 1000);
	private LocalDateTime time = LocalDateTime.now();

	private void addMilis(int milis) {
		time = time.plusNanos(milis * 1000 * 1000);
	}

	private void addFlow(int pulse) {
		buffer.newFlow(time, pulse);
	}

	private void addFlowAndTime(int pulse, int milis) {
		buffer.newFlow(time, pulse);
		addMilis(milis);
	}

	@Test
	public void threeTimes1000() {
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		assertEquals(1, repository.getSize());
		assertEquals(2000, repository.getFlowMeasurement(0).get(0));
		assertEquals(0, buffer.getBuffer());
	}
	
	@Test
	public void fourTimes1000_residue1000InBuffer() {
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		assertEquals(1, repository.getSize());
		assertEquals(2000, repository.getFlowMeasurement(0).get(0));
		assertEquals(1000, buffer.getBuffer());
	}
}
