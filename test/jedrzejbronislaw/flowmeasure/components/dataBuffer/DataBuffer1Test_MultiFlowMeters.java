package jedrzejbronislaw.flowmeasure.components.dataBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import jedrzejbronislaw.flowmeasure.components.dataBuffer.DataBuffer1;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public class DataBuffer1Test_MultiFlowMeters {
	
	private static final int BUFFER_TIME       = 1000;
	private static final int FLOWMETERS_NUMBER = 3;
	
	private ProcessRepository repository = new ProcessRepository(FLOWMETERS_NUMBER, "");
	private DataBuffer1 buffer = new DataBuffer1(repository, BUFFER_TIME);
	private LocalDateTime time = LocalDateTime.now();

	private void addMilis(long milis) {
		time = time.plusNanos(milis * 1000 * 1000);
	}

	private void addFlow(int[] pulse) {
		buffer.newFlow(time, pulse);
	}

	private void addFlowAndTime(int[] pulse, int milis) {
		buffer.newFlow(time, pulse);
		addMilis(milis);
	}

	private int[] getMeasurement(int index) {
		int[] measurement = new int[FLOWMETERS_NUMBER];
		
		for(int i=0; i<FLOWMETERS_NUMBER; i++)
			measurement[i] = repository.getFlowMeasurement(index).get(i);
		
		return measurement;
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
		int[] expectedBuffer = new int[]{0, 0, 0};
		
		assertEquals(0, repository.getSize());
		assertArrayEquals(expectedBuffer, buffer.getBuffer());
		
		checkTime();
	}

	@Test
	public void toManyTime() {
		int[] expectedBuffer =
				       new int[]{400, 130, 250};
		
		addFlowAndTime(new int[]{100,  20,  50}, 100);
		addFlowAndTime(new int[]{100,  30,  50}, 100);
		addFlowAndTime(new int[]{ 50,  30,  50}, 200);
		addFlowAndTime(new int[]{ 50,  20,  50}, 250);
		addFlowAndTime(new int[]{100,  20,  50}, 200);
		addFlow(       new int[]{100,  30,  50});
		
		assertEquals(0, repository.getSize());
		assertArrayEquals(expectedBuffer, buffer.getBuffer());
		
		checkTime();
	}
	
	@Test
	public void threeTimes1000() {
		int[] expectedBuffer =
                       new int[]{   0,    0,    0};
		int[] expectedMeasurnemt =
                       new int[]{2000, 2000, 2000};
		
		addFlowAndTime(new int[]{1000, 1000, 1000}, 500);
		addFlowAndTime(new int[]{1000, 1000, 1000}, 500);
		addFlow(       new int[]{1000, 1000, 1000});
		
		assertEquals(1, repository.getSize());
		assertArrayEquals(expectedMeasurnemt, getMeasurement(0));
		assertArrayEquals(expectedBuffer, buffer.getBuffer());
		
		checkTime();
	}
}
