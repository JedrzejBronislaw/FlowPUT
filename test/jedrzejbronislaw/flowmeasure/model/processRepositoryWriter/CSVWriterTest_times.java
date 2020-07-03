package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class CSVWriterTest_times {

	private static final LocalDateTime startTime = LocalDateTime.of(2020, 7, 3, 16, 7, 25);
	private static final long unixTime = 1593785245;

	//ProcessTime
	
	@Test
	public void testProcessTime_0sec() {
		String time = ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(0), startTime);
		assertEquals("0", time);
	}
	
	@Test
	public void testProcessTime_1sec() {
		String time = ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(1), startTime);
		assertEquals("1", time);
	}
	
	@Test
	public void testProcessTime_100sec() {
		String time = ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(100), startTime);
		assertEquals("100", time);
	}
	
	@Test
	public void testProcessTime_100kSec() {
		String time = ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(100000), startTime);
		assertEquals("100000", time);
	}
	
	@Test(expected = NullPointerException.class)
	public void testProcessTime_nullTime() {
		ProcessRepositoryCSVWriter.processTime(null, startTime);
	}
	
	@Test(expected = NullPointerException.class)
	public void testProcessTime_nullStartTime() {
		ProcessRepositoryCSVWriter.processTime(startTime, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testProcessTime_nullBothTimes() {
		 ProcessRepositoryCSVWriter.processTime(null, null);
	}
	
	@Test
	public void testProcessTime_minus1Sec() {
		String time = ProcessRepositoryCSVWriter.processTime(startTime.minusSeconds(1), startTime);
		assertEquals("-1", time);
	}
	
	@Test
	public void testProcessTime_minus100Sec() {
		String time = ProcessRepositoryCSVWriter.processTime(startTime.minusSeconds(100), startTime);
		assertEquals("-100", time);
	}

	//FullTime
	
	@Test
	public void testFullTime() {
		String time = ProcessRepositoryCSVWriter.fullTime(startTime);
		assertEquals(ProcessRepositoryCSVWriter.FORMATTER.format(startTime), time);
	}

	@Test(expected = NullPointerException.class)
	public void testFullTime_null() {
		String time = ProcessRepositoryCSVWriter.fullTime(null);
		assertEquals(ProcessRepositoryCSVWriter.FORMATTER.format(startTime), time);
	}

	//UnixTime
	
	@Test
	public void testUnixTime() {
		String time = ProcessRepositoryCSVWriter.unixTime(startTime);
		assertEquals(String.valueOf(unixTime), time);
	}
	
	@Test
	public void testUnixTime_plus1sec() {
		String time = ProcessRepositoryCSVWriter.unixTime(startTime.plusSeconds(1));
		assertEquals(String.valueOf(unixTime+1), time);
	}
	
	@Test
	public void testUnixTime_plus100sec() {
		String time = ProcessRepositoryCSVWriter.unixTime(startTime.plusSeconds(100));
		assertEquals(String.valueOf(unixTime+100), time);
	}

	@Test(expected = NullPointerException.class)
	public void testUnixTime_null() {
		ProcessRepositoryCSVWriter.fullTime(null);
	}
}
