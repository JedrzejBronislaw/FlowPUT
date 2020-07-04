package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;

import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.FlowConverter1;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Columns;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;
import jedrzejbronislaw.flowmeasure.tools.NumberTools;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;
import lombok.Setter;

public class ProcessRepositoryCSVWriter implements ProcessRepositoryWriter {
	private static final ZoneOffset timeZoneOffset = OffsetDateTime.now().getOffset();
	
	public static final String TITLE = "Flow measurement";//"Pomiar przep�ywu";//TODO internationalization
	
	public static final String METADATA_HEAD = "Metadata";
	public static final String DATA_HEAD     = "Measurement";
	public static final String PROP_NAME       = "name";
	public static final String PROP_AUTHOR     = "author";
	public static final String PROP_START      = "start";
	public static final String PROP_END        = "end";
	public static final String PROP_DURATION   = "duration";
	public static final String PROP_PULSE      = "pulse per litre";
	public static final String PROP_FLOWMETERS = "num of flowmeters";
	public static final String PROP_SIZE       = "size";
	
	public static final String UNIX_TIME_HEAD    = "unix time";
	public static final String FULL_TIME_HEAD    = "full time";
	public static final String PROCESS_TIME_HEAD = "process time [s]";
	
	public static final String DEF_FLOWMETER_NAME = "flowmeter";
	
	public static final String PULSE_COLUMNNAME = "p";
	public static final String  FLOW_COLUMNNAME = "f";

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private float pulsePerLitre = 0;
	
	private FlowConverter flowConverter;
	private ProcessRepositoryWriterOptions options;
	private CSVWriter csvWriter;
	private ProcessRepository repository;
	private List<FlowMeasurement> measurements;
	
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	
	@Setter
	private Function<File, Writer> writerCreator = file -> {
		try {
			return new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	};
	
	@Override
	public void setPulsePerLitre(float pulsePerLitre) {
		this.pulsePerLitre = pulsePerLitre;
		flowConverter = new FlowConverter1(pulsePerLitre);
	}
	
	public static String processTime(LocalDateTime time, LocalDateTime startTime) {
		return Long.toString(ChronoUnit.SECONDS.between(startTime, time));
	}

	public static String fullTime(LocalDateTime time) {
		return time.format(FORMATTER);
	}

	public static String unixTime(LocalDateTime time) {
		return Long.toString(time.toEpochSecond(timeZoneOffset));
	}
	
	@Override
	public boolean save(ProcessRepository repository, File file, ProcessRepositoryWriterOptions options) {
		this.options    = options;
		this.repository = repository;
		measurements    = repository.getAllMeasurementCopy();
		startTime       = repository.getMetadata().getStartTime();
		endTime         = repository.getMetadata().getEndTime();
		
		try {
			
			csvWriter = new CSVWriter(writerCreator.apply(file));
			writeFile();
			csvWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private void writeFile() throws IOException {
		csvWriter.line(TITLE);
		csvWriter.newLine();

		writeMetadata();
		csvWriter.newLine();
		writeData();
	}

	private void writeData() throws IOException {
		csvWriter.line(DATA_HEAD);

		writeDataHeader();
		for (FlowMeasurement m : measurements) writeMeasurementLine(m);
	}

	private void writeMetadata() throws IOException {
		String processName = repository.getMetadata().getName();
		String author = repository.getMetadata().getAuthor();
		
		csvWriter.line(METADATA_HEAD);

		csvWriter.property(PROP_NAME, processName);
		csvWriter.property(PROP_AUTHOR, author);
		csvWriter.property(PROP_START, startTime);
		csvWriter.property(PROP_END, endTime);
		csvWriter.property(PROP_DURATION, TimeCalc.createDurationString(startTime, endTime));
		csvWriter.newLine();

		csvWriter.property(PROP_PULSE, pulsePerLitre);
		csvWriter.newLine();

		csvWriter.property(PROP_FLOWMETERS, numOfFlowmeters());
		csvWriter.property(PROP_SIZE, repository.getSize());
	}

	private void writeDataHeader() throws IOException {
		if(numOfColumns() > 1)
			if(flowmeterValuesTogether())
				writeHeader_ManyColumns_FlowmeterTogther(); else
				writeHeader_ManyColumns_FlowmeterNotTogther();
		else
			writeHeader_OneColumn();
	}

	private void writeHeader_OneColumn() throws IOException {
		csvWriter.writeSeparators(numOfTimeFormats());
		csvWriter.write(columnName(0));
		csvWriter.newLine();

		writeTimeHeadres();
		for(int i=0; i<numOfFlowmeters(); i++)
			csvWriter.writeWithSeparator(flowmeterName(i));

		csvWriter.newLine();
	}

	private void writeHeader_ManyColumns_FlowmeterNotTogther() throws IOException {
		csvWriter.writeSeparators(numOfTimeFormats());
		csvWriter.write(columnName(0));
		csvWriter.writeSeparators(numOfFlowmeters());
		csvWriter.write(columnName(1));
		csvWriter.newLine();

		writeTimeHeadres();
		for(int i=0; i<numOfColumns(); i++)
			for(int j=0; j<numOfFlowmeters(); j++)
				csvWriter.writeWithSeparator(flowmeterName(j));

		csvWriter.newLine();
	}

	private void writeHeader_ManyColumns_FlowmeterTogther() throws IOException {
		csvWriter.writeSeparators(numOfTimeFormats());
		for(int i=0; i<numOfFlowmeters(); i++) {
			csvWriter.write(flowmeterName(i));
			csvWriter.writeSeparators(numOfColumns());
		}
		csvWriter.newLine();
		
		writeTimeHeadres();
		for(int i=0; i<numOfFlowmeters(); i++) {
			csvWriter.writeWithSeparator(columnName(0));
			csvWriter.writeWithSeparator(columnName(1));
		}

		csvWriter.newLine();
	}

	private void writeTimeHeadres() throws IOException {
		if(isSelected(TimeFormat.Unix))        csvWriter.writeWithSeparator(UNIX_TIME_HEAD);
		if(isSelected(TimeFormat.Full))        csvWriter.writeWithSeparator(FULL_TIME_HEAD);
		if(isSelected(TimeFormat.ProcessTime)) csvWriter.writeWithSeparator(PROCESS_TIME_HEAD);
	}
	
	private String columnName(int index) {
		return columnName(getColumn(index));
	}
	
	private String columnName(Columns column) {
		if (column == Columns.Pulses) return PULSE_COLUMNNAME;
		if (column == Columns.Flow)   return FLOW_COLUMNNAME;
		return "";
	}

	private void writeMeasurementLine(FlowMeasurement measurement) throws IOException {
		List<Columns> columns = options.getColumns();
		LocalDateTime time = measurement.getTime();
		flowConverter.newDataEvent(time);

		if(isSelected(TimeFormat.Unix))        csvWriter.writeWithSeparator(unixTime(time));
		if(isSelected(TimeFormat.Full))        csvWriter.writeWithSeparator(fullTime(time));
		if(isSelected(TimeFormat.ProcessTime)) csvWriter.writeWithSeparator(processTime(time, startTime));


		if(flowmeterValuesTogether())
			writeMeasurementFlowmeterTogether(measurement, columns); else
			writeMeasurementColumnTogether(measurement, columns);
		
		csvWriter.newLine();
	}

	private void writeMeasurementColumnTogether(FlowMeasurement measurement, List<Columns> columns) throws IOException {

		for (Columns column : columns)
			for(int i=0; i<numOfFlowmeters(); i++)
				writeMeasurement(column, measurement.get(i));
	}

	private void writeMeasurementFlowmeterTogether(FlowMeasurement measurement, List<Columns> columns) throws IOException {

		for(int i=0; i<numOfFlowmeters(); i++)
			for (Columns column : columns)
				writeMeasurement(column, measurement.get(i));
	}

	private void writeMeasurement(Columns column, int pulses) throws IOException {
		if (column == Columns.Pulses) writePulses(pulses);
		if (column == Columns.Flow)   writeFlow(pulses);
	}

	private void writePulses(int pulses) throws IOException {
		csvWriter.writeWithSeparator(pulses);
	}
	
	private void writeFlow(int pulses) throws IOException {
		Float flow = flowConverter.pulsesToLitrePerSec(pulses);
		csvWriter.writeWithSeparator(setDecimalSeparator(flow));
	}

	private String setDecimalSeparator(Float flow) {
		if(flow == null) return "";
		
		String decimalSeparator = options.isCommaSeparator() ? "," : ".";
		return NumberTools.floatToString(flow, decimalSeparator);
	}
	
	private int numOfTimeFormats() {
		return options.getTimeFormats().size();
	}

	private boolean flowmeterValuesTogether() {
		return options.isFlowmeterValuesTogether();
	}

	private int numOfColumns() {
		return options.getColumns().size();
	}

	private int numOfFlowmeters() {
		return repository.getNumOfFlowmeters();
	}
	
	private Columns getColumn(int index) {
		return options.getColumns().get(index);
	}
	
	private boolean isSelected(TimeFormat timeformat) {
		return options.getTimeFormats().contains(timeformat);
	}

	private String flowmeterName(int index) {
		return DEF_FLOWMETER_NAME + Integer.toString(index+1);
	}
}
