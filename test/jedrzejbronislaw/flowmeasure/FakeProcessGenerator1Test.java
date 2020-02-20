package jedrzejbronislaw.flowmeasure;

import static org.junit.Assert.assertEquals;

import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessMetadata;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public class FakeProcessGenerator1Test {

	int numOfFlowmeters = 6;
		
	FakeProcessGenerator generator;
	ProcessRepository process;

	
	@Before
	public void before() {
		generator = new FakeProcessGenerator1();
		process = new ProcessRepository(numOfFlowmeters, "");
	}
	
	
	@Test(expected = IllegalStateException.class)
	public void testGenerate_illegalStateException() {
		
		generator.generate(process, 100);
	}

	@Test
	public void testGenerate_100items() {		
		generator.setNumOfFlowmeters(numOfFlowmeters);
		generator.setInterval(1000);
		
		generator.generate(process, 100);
		
		assertEquals(100, process.getSize());
	}

	@Test
	public void testGenerate_timeSecondItem() {		
		generator.setNumOfFlowmeters(numOfFlowmeters);
		generator.setInterval(3300);
		
		generator.generate(process, 10);
		
		assertEquals(3300, ChronoUnit.MILLIS.between(
				process.getFlowMeasurement(0).getTime(),
				process.getFlowMeasurement(1).getTime()));
	}

	@Test
	public void testGenerate_time60items1sek() {		
		generator.setNumOfFlowmeters(numOfFlowmeters);
		generator.setInterval(1000);
		
		generator.generate(process, 60);
		
		ProcessMetadata metadata = process.getMetadata();
		assertEquals(59, ChronoUnit.SECONDS.between(metadata.getStartTime(), metadata.getEndTime()));
	}

	@Test
	public void testGenerate_time100items5sek() {		
		generator.setNumOfFlowmeters(numOfFlowmeters);
		generator.setInterval(5000);
		
		generator.generate(process, 100);
		
		ProcessMetadata metadata = process.getMetadata();
		assertEquals(495, ChronoUnit.SECONDS.between(metadata.getStartTime(), metadata.getEndTime()));
	}
	
	@Test
	public void testGenerate_time100000() {
		generator.setNumOfFlowmeters(numOfFlowmeters);
		generator.setInterval(1000);
		
		generator.generate(process, 100000);
		
		assertEquals(100000, process.getSize());
	}

}
