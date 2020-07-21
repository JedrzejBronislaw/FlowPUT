package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.BeforeClass;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;

public class CSVWriterTest_allMetadata {
	
	private static Parser parser;
	
	private static String AUTHOR = "Andy";
	private static String MEASUREMENT_NAME = "Measurement name";
	private static LocalDateTime startTime = LocalDateTime.of(2020, 7, 3, 14, 22, 11);
	private static LocalDateTime endTime   = LocalDateTime.of(2020, 8, 5, 17, 21, 30);
	
	@BeforeClass
	public static void prepare() {
		StringFile file = new StringFile(createRepository(), createOptions());
		file.setPulsePerLitre(new float[]{350, 350});
		file.setBufferInterval(1500);
		parser = new Parser(file.createContentOfFile());
	}
	
	private static ProcessRepositoryWriterOptions createOptions() {
		ProcessRepositoryWriterOptions options = new ProcessRepositoryWriterOptions();
		
		options.getUnits().add(Unit.Pulses);
		
		return options;
	}

	private static ProcessRepository createRepository() {
		ProcessRepository repository = new ProcessRepository(2, MEASUREMENT_NAME);

		repository.getMetadata().setAuthor(AUTHOR);
		repository.getMetadata().setStartTime(startTime);
		repository.getMetadata().setEndTime(endTime);
		
		repository.addFlowMeasurement(new int[]{0,0});
		repository.addFlowMeasurement(new int[]{1,2});
		repository.addFlowMeasurement(new int[]{2,4});
		
		return repository;
	}

	private void checkProperty(String name, String value) {
		assertTrue(parser.propertyExists(name));
		
		if(value != null) {
			assertEquals(2, parser.getProperty(name).length);
			assertEquals(value, parser.getProperty(name)[1]);
		} else
			assertEquals(1, parser.getProperty(name).length);
	}
	
	@Test
	public void property_name() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_NAME, MEASUREMENT_NAME);
	}
	
	@Test
	public void property_author() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_AUTHOR, AUTHOR);
	}
	
	@Test
	public void property_start() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_START,
				ProcessRepositoryCSVWriter.FORMATTER.format(startTime));
	}
	
	@Test
	public void property_end() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_END,
				ProcessRepositoryCSVWriter.FORMATTER.format(endTime));
	}
	
	@Test
	public void property_duration() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_DURATION,
				TimeCalc.createDurationString(startTime, endTime));
	}
	
	@Test
	public void property_buffer() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_BUFFER, "1500");
	}
	
	@Test
	public void property_pulse() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_PULSE + 1, "350.0");
		checkProperty(ProcessRepositoryCSVWriter.PROP_PULSE + 2, "350.0");
	}
	
	@Test
	public void property_flowmeters() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_FLOWMETERS, "2");
	}
	
	@Test
	public void property_size() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_SIZE, "3");
	}
	
	@Test
	public void property_abcd() {
		assertFalse(parser.propertyExists("abcd"));
	}
}
