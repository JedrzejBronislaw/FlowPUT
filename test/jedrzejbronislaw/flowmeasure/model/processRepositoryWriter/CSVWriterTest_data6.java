package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.BeforeClass;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowUnit;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;

public class CSVWriterTest_data6 {
	
	private static Parser parser;
	
	private static final String AUTHOR = "Andy";
	private static final String MEASUREMENT_NAME = "Measurement name";
	private static final float[] pulsePerLitre = new float[]{350, 350};
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
		
		options.setFlowmeterValuesTogether(true);
		
		options.getUnits().add(Unit.FLOW);
		options.getUnits().add(Unit.PULSES);
		
		options.getTimeFormats().add(TimeFormat.FULL);
		options.getTimeFormats().add(TimeFormat.PROCESS_TIME);
		
		options.setFlowUnit(FlowUnit.LITER_PER_SECOND);
		
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
		
		assertEquals(5, firstLine.length);
		
		assertEquals("", firstLine[0]);
		assertEquals("", firstLine[1]);
		assertEquals(ProcessRepositoryCSVWriter.DEF_FLOWMETER_NAME + " 1", firstLine[2]);
		assertEquals("", firstLine[3]);
		assertEquals(ProcessRepositoryCSVWriter.DEF_FLOWMETER_NAME + " 2", firstLine[4]);
	}
	
	@Test
	public void secondLineHeader() {
		String[] secondLine = parser.getDataHeader().get(1);
		
		assertEquals(6, secondLine.length);
		
		assertEquals(ProcessRepositoryCSVWriter.FULL_TIME_HEAD,    secondLine[0]);
		assertEquals(ProcessRepositoryCSVWriter.PROCESS_TIME_HEAD, secondLine[1]);
		assertTrue(secondLine[2].startsWith(ProcessRepositoryCSVWriter.FLOW_COLUMNNAME));
		assertEquals(ProcessRepositoryCSVWriter.PULSE_COLUMNNAME,  secondLine[3]);
		assertTrue(secondLine[4].startsWith(ProcessRepositoryCSVWriter.FLOW_COLUMNNAME));
		assertEquals(ProcessRepositoryCSVWriter.PULSE_COLUMNNAME,  secondLine[5]);
	}
	
	@Test
	public void firstLineData() {
		String[] line = parser.getData().get(0);
		
		assertEquals(6, line.length);
		
		assertEquals(ProcessRepositoryCSVWriter.fullTime(startTime.plusSeconds(0)), line[0]);
		assertEquals(ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(0), startTime), line[1]);
		
		assertEquals("",  line[2]);
		assertEquals("0", line[3]);
		assertEquals("",  line[4]);
		assertEquals("0", line[5]);
	}
	
	@Test
	public void secondLineData() {
		String[] line = parser.getData().get(1);
		
		assertEquals(6, line.length);
		
		assertEquals(ProcessRepositoryCSVWriter.fullTime(startTime.plusSeconds(1)), line[0]);
		assertEquals(ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(1), startTime), line[1]);
		
		assertEquals(1/pulsePerLitre[0], Float.parseFloat(line[2]), deltaFlow);
		assertEquals("1", line[3]);
		assertEquals(2/pulsePerLitre[1], Float.parseFloat(line[4]), deltaFlow);
		assertEquals("2", line[5]);
	}
	
	@Test
	public void thirdLineData() {
		String[] line = parser.getData().get(2);
		
		assertEquals(6, line.length);
		
		assertEquals(ProcessRepositoryCSVWriter.fullTime(startTime.plusSeconds(2)), line[0]);
		assertEquals(ProcessRepositoryCSVWriter.processTime(startTime.plusSeconds(2), startTime), line[1]);
		
		assertEquals(2/pulsePerLitre[0], Float.parseFloat(line[2]), deltaFlow);
		assertEquals("2", line[3]);
		assertEquals(4/pulsePerLitre[1], Float.parseFloat(line[4]), deltaFlow);
		assertEquals("4", line[5]);
	}
	
	@Test
	public void numberOfDataLine() {
		assertEquals(3, parser.getData().size());
	}
}
