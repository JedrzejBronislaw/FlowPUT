package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.BeforeClass;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;

public class CSVWriterTest_data4 {
	
	private static Parser parser;
	
	private static final String AUTHOR = "Andy";
	private static final String MEASUREMENT_NAME = "Measurement name";
	private static final float pulsePerLitre = 350;
	private static final LocalDateTime startTime = LocalDateTime.of(2020, 7, 3, 14, 22, 11);
	private static final LocalDateTime endTime   = LocalDateTime.of(2020, 8, 5, 17, 21, 30);

	private static final double deltaFlow = 0.0000001;
	
	@BeforeClass
	public static void prepare() {
		StringFile file = new StringFile(createRepository(), createOptions());
		file.setPulsePerLitre(pulsePerLitre);
		parser = new Parser(file.createContentOfFile());
	}
	
	private static ProcessRepositoryWriterOptions createOptions() {
		ProcessRepositoryWriterOptions options = new ProcessRepositoryWriterOptions();

		options.setFlowmeterValuesTogether(false);
		
		options.getUnits().add(Unit.Flow);
		options.getUnits().add(Unit.Pulses);
		
		options.getTimeFormats().add(TimeFormat.ProcessTime);
		
		return options;
	}

	private static ProcessRepository createRepository() {
		ProcessRepository repository = new ProcessRepository(2, MEASUREMENT_NAME);

		repository.getMetadata().setAuthor(AUTHOR);
		repository.getMetadata().setStartTime(startTime);
		repository.getMetadata().setEndTime(endTime);
		
		repository.addFlowMeasurement(startTime.plusSeconds(0), new int[]{0, 0});
		repository.addFlowMeasurement(startTime.plusSeconds(1), new int[]{1, 2});
		repository.addFlowMeasurement(startTime.plusSeconds(2), new int[]{2, 4});
		
		return repository;
	}

	@Test
	public void headerLines() {
		assertEquals(2, parser.getDataHeader().size());
	}
	
	@Test
	public void firstLineHeader() {
		String[] firstLine = parser.getDataHeader().get(0);
		
		assertEquals(4, firstLine.length);
		
		assertEquals("", firstLine[0]);
		assertEquals(ProcessRepositoryCSVWriter.FLOW_COLUMNNAME,  firstLine[1]);
		assertEquals("", firstLine[2]);
		assertEquals(ProcessRepositoryCSVWriter.PULSE_COLUMNNAME, firstLine[3]);
	}
	
	@Test
	public void secondLineHeader() {
		String[] secondLine = parser.getDataHeader().get(1);
		
		assertEquals(5, secondLine.length);
		
		assertEquals(ProcessRepositoryCSVWriter.PROCESS_TIME_HEAD, secondLine[0]);
		assertEquals(ProcessRepositoryCSVWriter.DEF_FLOWMETER_NAME + "1", secondLine[1]);
		assertEquals(ProcessRepositoryCSVWriter.DEF_FLOWMETER_NAME + "2", secondLine[2]);
		assertEquals(ProcessRepositoryCSVWriter.DEF_FLOWMETER_NAME + "1", secondLine[3]);
		assertEquals(ProcessRepositoryCSVWriter.DEF_FLOWMETER_NAME + "2", secondLine[4]);
	}
	
	@Test
	public void firstLineData() {
		String[] line = parser.getData().get(0);
		
		assertEquals(5, line.length);
		
		assertEquals(ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(0), startTime), line[0]);
		assertEquals("",  line[1]);
		assertEquals("",  line[2]);
		assertEquals("0", line[3]);
		assertEquals("0", line[4]);
	}
	
	@Test
	public void secondLineData() {
		String[] line = parser.getData().get(1);
		
		assertEquals(5, line.length);
		
		assertEquals(ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(1), startTime), line[0]);
		assertEquals(1/pulsePerLitre, Float.parseFloat(line[1]), deltaFlow);
		assertEquals(2/pulsePerLitre, Float.parseFloat(line[2]), deltaFlow);
		assertEquals("1", line[3]);
		assertEquals("2", line[4]);
	}
	
	@Test
	public void thirdLineData() {
		String[] line = parser.getData().get(2);
		
		assertEquals(5, line.length);
		
		assertEquals(ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(2), startTime), line[0]);
		assertEquals(2/pulsePerLitre, Float.parseFloat(line[1]), deltaFlow);
		assertEquals(4/pulsePerLitre, Float.parseFloat(line[2]), deltaFlow);
		assertEquals("2", line[3]);
		assertEquals("4", line[4]);
	}
	
	@Test
	public void numberOfDataLine() {
		assertEquals(3, parser.getData().size());
	}
}