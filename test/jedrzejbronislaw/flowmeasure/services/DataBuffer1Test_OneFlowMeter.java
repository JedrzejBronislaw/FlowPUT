package jedrzejbronislaw.flowmeasure.services;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public class DataBuffer1Test_OneFlowMeter {
	
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
	
	private void checkRepositorySize(int expectedSize) {
		assertEquals(expectedSize, repository.getSize());
	}
	
	private void checkMeasurement(int expectedValue, int measurement) {
		assertEquals(expectedValue, getMeasurement(measurement));
	}
	
	private void checkBuffer(int expectedBuffer) {
		assertEquals(expectedBuffer, buffer.getBuffer()[0]);
	}

	@Test
	public void noAction() {
		checkRepositorySize(0);
		checkBuffer(0);
		
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
		
		checkRepositorySize(0);
		checkBuffer(500);
		
		checkTime();
	}
	
	@Test
	public void threeTimes1000() {
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		checkRepositorySize(1);
		checkMeasurement(2000, 0);
		checkBuffer(0);
		
		checkTime();
	}

	@Test
	public void fourTimes1000_residue1000InBuffer() {
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlowAndTime(1000, 500);
		addFlow(1000);
		
		checkRepositorySize(1);
		checkMeasurement(2000, 0);
		checkBuffer(1000);
		
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
		
		checkRepositorySize(2);
		checkMeasurement(2000, 0);
		checkMeasurement(2000, 1);
		checkBuffer(1000);
		
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
		
		checkRepositorySize(2);
		checkMeasurement(0, 0);
		checkMeasurement(0, 1);
		checkBuffer(0);
		
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
		
		checkRepositorySize(2);
		checkMeasurement(   0, 0);
		checkMeasurement(2000, 1);
		checkBuffer(1000);
		
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
		
		checkRepositorySize(1);
		checkMeasurement(3333, 0);
		checkBuffer(1667);
		
		checkTime();
	}
	
	@Test
	public void timeEqualsBufferTime() {
		addFlowAndTime(1000, BUFFER_TIME);
		addFlowAndTime(1000, BUFFER_TIME);
		addFlowAndTime(1000, BUFFER_TIME);
		addFlowAndTime(1000, BUFFER_TIME);
		addFlow(1000);
		
		checkRepositorySize(4);
		checkMeasurement(1000, 0);
		checkMeasurement(1000, 1);
		checkMeasurement(1000, 2);
		checkMeasurement(1000, 3);
		checkBuffer(0);
		
		checkTime();
	}
	
	@Test
	public void timeGiggerThanBufferTime() {
		addFlowAndTime(1000, 2000);
		addFlowAndTime(1000, 2000);
		addFlowAndTime(1000, 4000);
		addFlowAndTime(1000, 1500);
		addFlow(1000);
		
		checkRepositorySize(9);
		checkMeasurement(500, 0);
		checkMeasurement(500, 1);
		checkMeasurement(500, 2);
		checkMeasurement(500, 3);
		checkMeasurement(250, 4);
		checkMeasurement(250, 5);
		checkMeasurement(250, 6);
		checkMeasurement(250, 7);
		checkMeasurement(667, 8);
		checkBuffer(333);
		
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
		
		checkRepositorySize(9);
		checkMeasurement(1000, 0);
		checkMeasurement(1000, 1);
		checkMeasurement(1000, 2);
		checkMeasurement(1000, 3);
		checkMeasurement(1000, 4);
		checkMeasurement(1000, 5);
		checkMeasurement(1000, 6);
		checkMeasurement(1000, 7);
		checkMeasurement(1000, 8);
		checkBuffer(0);
		
		checkTime();
	}
}
