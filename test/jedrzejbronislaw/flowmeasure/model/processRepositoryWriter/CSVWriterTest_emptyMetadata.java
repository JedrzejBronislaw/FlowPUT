package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;

public class CSVWriterTest_emptyMetadata {
	
	private static Parser parser;
	
	@BeforeClass
	public static void prepare() {
		StringFile file = new StringFile(createRepository(), createOptions());
		parser = new Parser(file.createContentOfFile());
	}
	
	private static ProcessRepositoryWriterOptions createOptions() {
		ProcessRepositoryWriterOptions options = new ProcessRepositoryWriterOptions();
		
		options.getUnits().add(Unit.PULSES);
		
		return options;
	}

	private static ProcessRepository createRepository() {
		ProcessRepository repository = new ProcessRepository(2, "");
		
		return repository;
	}

	private void checkProperty(String name, String value) {
		assertTrue(parser.propertyExists(name));
		
		if (value != null) {
			assertEquals(2, parser.getProperty(name).length);
			assertEquals(value, parser.getProperty(name)[1]);
		} else
			assertEquals(1, parser.getProperty(name).length);
	}
	
	@Test
	public void property_name() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_NAME, null);
	}
	
	@Test
	public void property_author() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_AUTHOR, null);
	}
	
	@Test
	public void property_start() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_START, null);
	}
	
	@Test
	public void property_end() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_END, null);
	}
	
	@Test
	public void property_duration() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_DURATION, null);
	}
	
	@Test
	public void property_buffer() {
		assertFalse(parser.propertyExists(ProcessRepositoryCSVWriter.PROP_BUFFER));
	}
	
	@Test
	public void property_pulse() {
		assertFalse(parser.propertyExists(ProcessRepositoryCSVWriter.PROP_PULSE + "0"));
		assertFalse(parser.propertyExists(ProcessRepositoryCSVWriter.PROP_PULSE + "1"));
	}
	
	@Test
	public void property_flowmeters() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_FLOWMETERS, "2");
	}
	
	@Test
	public void property_size() {
		checkProperty(ProcessRepositoryCSVWriter.PROP_SIZE, "0");
	}
	
	@Test
	public void property_abcd() {
		assertFalse(parser.propertyExists("abcd"));
	}
}
