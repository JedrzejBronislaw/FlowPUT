package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Columns;

public class CSVWriterTest_simpleMetadata {
	
	private static Parser parser;
	
	@BeforeClass
	public static void prepare() {
		StringFile file = new StringFile(createRepository(), createOptions());
		parser = new Parser(file.createContentOfFile());
	}
	
	private static ProcessRepositoryWriterOptions createOptions() {
		ProcessRepositoryWriterOptions options = new ProcessRepositoryWriterOptions();
		
		options.getColumns().add(Columns.Pulses);
		
		return options;
	}

	private static ProcessRepository createRepository() {
		ProcessRepository repository = new ProcessRepository(2, "Measurement name");
		repository.addFlowMeasurement(new int[]{0,0});
		repository.addFlowMeasurement(new int[]{1,2});
		repository.addFlowMeasurement(new int[]{2,4});
		
		return repository;
	}

	public void checkProperty(String name, String value) {
		assertTrue(parser.propertyExists(name));
		
		if(value != null) {
			assertEquals(2, parser.getProperty(name).length);
			assertEquals(value, parser.getProperty(name)[1]);
		} else
			assertEquals(1, parser.getProperty(name).length);
	}
	
	@Test
	public void property_name() {
		checkProperty("name", "Measurement name");
	}
	
	@Test
	public void property_author() {
		checkProperty("author", null);
	}
	
	@Test
	public void property_start() {
		checkProperty("start", null);
	}
	
	@Test
	public void property_end() {
		checkProperty("end", null);
	}
	
	@Test
	public void property_duration() {
		checkProperty("duration", null);
	}
	
	@Test
	public void property_pulse() {
		checkProperty("pulse per litre", "0.0");
	}
	
	@Test
	public void property_flowmeters() {
		checkProperty("num of flowmeters", "2");
	}
	
	@Test
	public void property_size() {
		checkProperty("size", "3");
	}
	
	@Test
	public void property_abcd() {
		assertFalse(parser.propertyExists("abcd"));
	}
}
