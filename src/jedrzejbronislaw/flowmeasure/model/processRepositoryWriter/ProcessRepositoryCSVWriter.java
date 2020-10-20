package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;
import jedrzejbronislaw.flowmeasure.tools.NumberTools;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;
import lombok.Setter;

public class ProcessRepositoryCSVWriter implements ProcessRepositoryWriter {
	private static final ZoneOffset TIME_ZONE_OFFSET = OffsetDateTime.now().getOffset();
	private static final DecimalFormat PROCESS_TIME_PRECISION = new DecimalFormat("#.#");
	
	public static final String TITLE = "Flow measurement";
	
	public static final String METADATA_HEAD = "Metadata";
	public static final String DATA_HEAD     = "Measurement";
	public static final String PROP_NAME       = "name";
	public static final String PROP_AUTHOR     = "author";
	public static final String PROP_START      = "start";
	public static final String PROP_END        = "end";
	public static final String PROP_DURATION   = "duration";
	public static final String PROP_BUFFER     = "buffer [ms]";
	public static final String PROP_FLOWMETERS = "num of flowmeters";
	public static final String PROP_SIZE       = "size";
	
	public static final String PROP_FLOWMETER  = "Flowmeter";
	public static final String PROP_FLOW_NAME  = "Name";
	public static final String PROP_PULSE      = "Pulses per litre";
	
	public static final String UNIX_TIME_HEAD    = "unix time";
	public static final String FULL_TIME_HEAD    = "full time";
	public static final String PROCESS_TIME_HEAD = "process time [s]";
	
	public static final String DEF_FLOWMETER_NAME = "Flowmeter";
	
	public static final String PULSE_COLUMNNAME = "pulse";
	public static final String  FLOW_COLUMNNAME = "flow";

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private String[] flowmeterNames = new String[0];
	private float[]  pulsePerLitre  = new float [0];
	private FlowmeterCSVWriter[] flowmeterWriters;
	private int bufferInterval  = 0;
	
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
	public void setFlowmeterNames(String[] names) {
		flowmeterNames = (names != null) ? names : new String[0];
	}
	
	@Override
	public void setPulsePerLitre(float[] ratios) {
		pulsePerLitre = (ratios != null) ? ratios : new float[0];
	}
	
	
	private void createFlowmeterWriters() {
		if (pulsePerLitre == null) return;
		
		int size = pulsePerLitre.length;
		flowmeterWriters = new FlowmeterCSVWriter[size];
		
		for(int i=0; i<size; i++)
			flowmeterWriters[i] = new FlowmeterCSVWriter(csvWriter, i, pulsePerLitre[i], options);
	}
	
	private void delFlowWriters() {
		flowmeterWriters = null;
	}
	
	@Override
	public void setBufferInterval(int interval) {
		bufferInterval = interval;
	}
	
	public static String processTime(LocalDateTime time, LocalDateTime startTime) {
		return processTime(time, startTime, ".");
	}
	
	private static String processTime(LocalDateTime time, LocalDateTime startTime, String decimalSeparator) {
		return NumberTools.floatToString(
				ChronoUnit.MILLIS.between(startTime, time) / 1000f,
				decimalSeparator,
				PROCESS_TIME_PRECISION
				);
	}

	public static String fullTime(LocalDateTime time) {
		return time.format(FORMATTER);
	}

	public static String unixTime(LocalDateTime time) {
		return Long.toString(time.toEpochSecond(TIME_ZONE_OFFSET));
	}
	
	@Override
	public boolean save(ProcessRepository repository, File file, ProcessRepositoryWriterOptions options) {
		this.options    = options;
		this.repository = repository;
		measurements    = repository.getAllMeasurement();
		startTime       = repository.getMetadata().getStartTime();
		endTime         = repository.getMetadata().getEndTime();
		
		try {
			
			csvWriter = new CSVWriter(writerCreator.apply(file));
			createFlowmeterWriters();
			
			writeFile();
			
			delFlowWriters();
			csvWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private void writeFile() throws IOException {
		if (options.isSaveMetadata()) writeTitleAndMetadata();
		writeData();
	}

	private void writeTitleAndMetadata() throws IOException {
		csvWriter.line(TITLE);
		csvWriter.newLine();
		
		writeMetadata();
		csvWriter.newLine();
	}

	private void writeData() throws IOException {
		if (options.isSaveHeaders()) {
			csvWriter.line(DATA_HEAD);
			writeDataHeader();
		}
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

		if (bufferInterval>0)
			csvWriter.property(PROP_BUFFER, bufferInterval);
		
		writeFlowmetersInfo();
		
		csvWriter.newLine();

		csvWriter.property(PROP_FLOWMETERS, numOfFlowmeters());
		csvWriter.property(PROP_SIZE, repository.getSize());
	}
	
	private void writeFlowmetersInfo() throws IOException {
		int size = Math.max(pulsePerLitre.length, flowmeterNames.length);
		
		csvWriter.writeWithSeparator(PROP_FLOWMETER);
		csvWriter.writeWithSeparator(PROP_FLOW_NAME);
		csvWriter.writeWithSeparator(PROP_PULSE);
		csvWriter.newLine();
		
		for (int i=0; i<size; i++) {
			csvWriter.writeWithSeparator(defaultFlowmeterName(i));
			csvWriter.writeWithSeparator((flowmeterNames.length > i) ? flowmeterNames[i] : "" );
			csvWriter.writeWithSeparator((pulsePerLitre. length > i) ? toString(pulsePerLitre[i]) : "");
			csvWriter.newLine();
		}
	}

	private void writeDataHeader() throws IOException {
		if(numOfUnits() > 1)
			if(flowmeterValuesTogether())
				writeHeader_ManyUnits_FlowmeterTogether(); else
				writeHeader_ManyUnits_UnitTogether();
		else
			writeHeader_OneUnit();
	}

	private void writeHeader_OneUnit() throws IOException {
		csvWriter.writeSeparators(numOfTimeFormats());
		csvWriter.write(unitName(0));
		csvWriter.newLine();

		writeTimeHeadres();
		for(int i=0; i<numOfFlowmeters(); i++)
			csvWriter.writeWithSeparator(flowmeterName(i));

		csvWriter.newLine();
	}

	private void writeHeader_ManyUnits_UnitTogether() throws IOException {
		csvWriter.writeSeparators(numOfTimeFormats());
		csvWriter.write(unitName(0));
		csvWriter.writeSeparators(numOfFlowmeters());
		csvWriter.write(unitName(1));
		csvWriter.newLine();

		writeTimeHeadres();
		for(int i=0; i<numOfUnits(); i++)
			for(int j=0; j<numOfFlowmeters(); j++)
				csvWriter.writeWithSeparator(flowmeterName(j));

		csvWriter.newLine();
	}

	private void writeHeader_ManyUnits_FlowmeterTogether() throws IOException {
		csvWriter.writeSeparators(numOfTimeFormats());
		for(int i=0; i<numOfFlowmeters(); i++) {
			csvWriter.write(flowmeterName(i));
			csvWriter.writeSeparators(numOfUnits());
		}
		csvWriter.newLine();
		
		writeTimeHeadres();
		for(int i=0; i<numOfFlowmeters(); i++) {
			csvWriter.writeWithSeparator(unitName(0));
			csvWriter.writeWithSeparator(unitName(1));
		}

		csvWriter.newLine();
	}

	private void writeTimeHeadres() throws IOException {
		if(isSelected(TimeFormat.UNIX))         csvWriter.writeWithSeparator(UNIX_TIME_HEAD);
		if(isSelected(TimeFormat.FULL))         csvWriter.writeWithSeparator(FULL_TIME_HEAD);
		if(isSelected(TimeFormat.PROCESS_TIME)) csvWriter.writeWithSeparator(PROCESS_TIME_HEAD);
	}

	private void writeMeasurementLine(FlowMeasurement measurement) throws IOException {
		List<Unit> units = options.getUnits();
		LocalDateTime time = measurement.getTime();
		String decimalSeparator = options.getDecimalSeparator().toString();
		Stream.of(flowmeterWriters).forEach(flowWriter -> flowWriter.newDataEvent(time));

		if(isSelected(TimeFormat.UNIX))         csvWriter.writeWithSeparator(unixTime(time));
		if(isSelected(TimeFormat.FULL))         csvWriter.writeWithSeparator(fullTime(time));
		if(isSelected(TimeFormat.PROCESS_TIME)) csvWriter.writeWithSeparator(processTime(time, startTime, decimalSeparator));


		if(flowmeterValuesTogether())
			writeMeasurementFlowmeterTogether(measurement, units); else
			writeMeasurementUnitTogether(measurement, units);
		
		csvWriter.newLine();
	}

	private void writeMeasurementUnitTogether(FlowMeasurement measurement, List<Unit> units) throws IOException {

		for (Unit unit : units)
			for(int i=0; i<numOfFlowmeters(); i++)
				flowmeterWriters[i].writeMeasurement(unit, measurement);
	}

	private void writeMeasurementFlowmeterTogether(FlowMeasurement measurement, List<Unit> units) throws IOException {

		for(int i=0; i<numOfFlowmeters(); i++)
			for (Unit unit : units)
				flowmeterWriters[i].writeMeasurement(unit, measurement);
	}

	private int numOfTimeFormats() {
		return options.getTimeFormats().size();
	}

	private boolean flowmeterValuesTogether() {
		return options.isFlowmeterValuesTogether();
	}

	private int numOfUnits() {
		return options.getUnits().size();
	}

	private int numOfFlowmeters() {
		return repository.getNumOfFlowmeters();
	}
	
	private Unit getUnit(int index) {
		return options.getUnits().get(index);
	}
	
	private boolean isSelected(TimeFormat timeformat) {
		return options.getTimeFormats().contains(timeformat);
	}
	
	private String defaultFlowmeterName(int index) {
		return DEF_FLOWMETER_NAME + " " + Integer.toString(index+1);
	}

	private String flowmeterName(int index) {
		return (flowmeterNames.length > index) ? flowmeterNames[index] : defaultFlowmeterName(index);
	}
	
	private String unitName(int index) {
		return unitName(getUnit(index));
	}
	
	private String unitName(Unit unit) {
		if (unit == Unit.PULSES) return PULSE_COLUMNNAME;
		if (unit == Unit.FLOW)   return flowColumnNameWithUnit();
		return "";
	}

	private String flowColumnNameWithUnit() {
		return FLOW_COLUMNNAME + " [" + options.getFlowUnit() + "]";
	}

	private String toString(float value) {
		return NumberTools.floatToString(value, options.getDecimalSeparator().toString());
	}
}
