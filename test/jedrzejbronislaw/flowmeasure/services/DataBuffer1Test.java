package jedrzejbronislaw.flowmeasure.services;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public class DataBuffer1Test {
	
	private static final int BUFFER_TIME = 1000;
	
	private ProcessRepository repository = new ProcessRepository(1, "");
	private DataBuffer1 buffer = new DataBuffer1(repository, BUFFER_TIME);
	private LocalDateTime time = LocalDateTime.now();

	private void addMilis(long milis) {
		time = time.plusNanos(milis * 1000 * 1000);
	}

	private void addFlow(int pulse) {
		buffer.newFlow(time, pulse);
	}

	private void addFlowAndTime(int pulse, int milis) {
		buffer.newFlow(time, pulse);
		addMilis(milis);
	}

	private int getMeasurement(int index) {
		return repository.getFlowMeasurement(index).get(0);
	}

	private LocalDateTime getTime(int index) {
		return repository.getFlowMeasurement(index).getTime();
	}
	
	private long timeBetween(LocalDateTime time1, LocalDateTime time2) {
		return ChronoUnit.MILLIS.between(time1, time2);
	}
	
	private void checkTime() {
		int size = repository.getSize();
		if (size == 0) return;
		
		LocalDateTime prevTime = getTime(0);
		LocalDateTime time;
		long interval;
		
		
		for (int i=1; i<size; i++) {
			time = getTime(i);
			interval = timeBetween(prevTime, time);
			assertEquals("wrong time (index: " + i + ")", BUFFER_TIME, interval);
			prevTime = time;
		}
	}

	@Test
	public void noAction() {
		assertEquals(0, repository.getSize());
		assertEquals(0, buffer.getBuffer());
		
		checkTime();
	}

	@Test
	public void toManyTime() {
		addFlowAndTime(100, 100);
		addFlowAndTime(100, 100);
		addFlowAndTime(100, 200);
		addFlowAndTime(100, 250);
		addFlowAndTime(100, 200);
		addFlow(100);
		
		assertEquals(0, repository.getSize());
		assertEquals(500, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void threeTimes1000() {
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		assertEquals(1, repository.getSize());
		assertEquals(2000, getMeasurement(0));
		assertEquals(   0, buffer.getBuffer());
		
		checkTime();
	}

	@Test
	public void fourTimes1000_residue1000InBuffer() {
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		assertEquals(1, repository.getSize());
		assertEquals(2000, getMeasurement(0));
		assertEquals(1000, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void sixTimes1000_2measure_residue1000InBuffer() {
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		assertEquals(2, repository.getSize());
		assertEquals(2000, getMeasurement(0));
		assertEquals(2000, getMeasurement(1));
		assertEquals(1000, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void sixTimes0_2measure_residue0InBuffer() {
		addFlowAndTime(0, 500);
		addFlowAndTime(0, 500);
		addFlowAndTime(0, 500);
		addFlowAndTime(0, 500);
		addFlowAndTime(0, 500);
		addFlow(0);
		
		assertEquals(2, repository.getSize());
		assertEquals(0, getMeasurement(0));
		assertEquals(0, getMeasurement(1));
		assertEquals(0, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void three0tree1000_2measure_residue1000InBuffer() {
		addFlowAndTime(0, 500);
		addFlowAndTime(0, 500);
		addFlowAndTime(0, 500);
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		assertEquals(2, repository.getSize());
		assertEquals(   0, getMeasurement(0));
		assertEquals(2000, getMeasurement(1));
		assertEquals(1000, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void pulse1000time300_1measure_residue1667InBuffer() {
		addFlowAndTime(1000, 300);
		addFlowAndTime(1000, 300);
		addFlowAndTime(1000, 300);
		addFlowAndTime(1000, 300);
		addFlowAndTime(1000, 300);
		addFlow(1000);
		
		assertEquals(1, repository.getSize());
		assertEquals(3333, getMeasurement(0));
		assertEquals(1667, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void timeEqualsBufferTime() {
		addFlowAndTime(1000, BUFFER_TIME);
		addFlowAndTime(1000, BUFFER_TIME);
		addFlowAndTime(1000, BUFFER_TIME);
		addFlowAndTime(1000, BUFFER_TIME);
		addFlow(1000);
		
		assertEquals(4, repository.getSize());
		assertEquals(1000, getMeasurement(0));
		assertEquals(1000, getMeasurement(1));
		assertEquals(1000, getMeasurement(2));
		assertEquals(1000, getMeasurement(3));
		assertEquals(   0, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void timeGiggerThanBufferTime() {
		addFlowAndTime(1000, 2000);
		addFlowAndTime(1000, 2000);
		addFlowAndTime(1000, 4000);
		addFlowAndTime(1000, 1500);
		addFlow(1000);
		
		assertEquals(9, repository.getSize());
		assertEquals(500, getMeasurement(0));
		assertEquals(500, getMeasurement(1));
		assertEquals(500, getMeasurement(2));
		assertEquals(500, getMeasurement(3));
		assertEquals(250, getMeasurement(4));
		assertEquals(250, getMeasurement(5));
		assertEquals(250, getMeasurement(6));
		assertEquals(250, getMeasurement(7));
		assertEquals(667, getMeasurement(8));
		assertEquals(333, buffer.getBuffer());
		
		checkTime();
	}

	@Test
	public void variousPulesAndTime() {
		addFlowAndTime(1000,  100);
		addFlowAndTime( 100,  350);
		addFlowAndTime( 350, 1045);
		addFlowAndTime(1045, 1600);
		addFlowAndTime(1600, 5612);
		addFlowAndTime(5612,    5);
		addFlowAndTime(   5,  288);
		addFlow(288);
		
		assertEquals(9, repository.getSize());
		assertEquals(1000, getMeasurement(0));
		assertEquals(1000, getMeasurement(1));
		assertEquals(1000, getMeasurement(2));
		assertEquals(1000, getMeasurement(3));
		assertEquals(1000, getMeasurement(4));
		assertEquals(1000, getMeasurement(5));
		assertEquals(1000, getMeasurement(6));
		assertEquals(1000, getMeasurement(7));
		assertEquals(1000, getMeasurement(8));
		assertEquals(   0, buffer.getBuffer());
		
		checkTime();
	}
}
