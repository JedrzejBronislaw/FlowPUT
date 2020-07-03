package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.FlowConverter1;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Columns;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;
import jedrzejbronislaw.flowmeasure.tools.TimeCalc;
import lombok.Setter;

public class ProcessRepositoryCSVWriter implements ProcessRepositoryWriter {
	public static final String TITLE = "Flow measurement";//"Pomiar przep³ywu";//TODO internationalization
	
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
	public static final String SEPARATOR = ";";
	public static final String NEW_LINE_SEPARATOR = "\n";
	
	@Setter
	private float pulsePerLitre = 0;
	
	private FlowConverter flowConverter;
	private ProcessRepositoryWriterOptions options;
	private Writer writer;
	private ProcessRepository repository;
	
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
	public boolean save(ProcessRepository repository, File file, ProcessRepositoryWriterOptions options) {
		flowConverter = new FlowConverter1(pulsePerLitre);
		this.options = options;
		this.repository = repository;
		
		try {
			String processName = repository.getMetadata().getName();
			String author = repository.getMetadata().getAuthor();
			writer = writerCreator.apply(file);
			startTime = repository.getMetadata().getStartTime();
			endTime = repository.getMetadata().getEndTime();
			
			List<FlowMeasurement> data = repository.getAllMeasurementCopy();


			writer.write(TITLE);
			newLine();

			newLine();

			writer.write(METADATA_HEAD);
			newLine();

			writer.write(PROP_NAME + SEPARATOR);
			if(processName != null)
				writer.write(processName);
			newLine();
			
			writer.write(PROP_AUTHOR + SEPARATOR);
			if(author != null)
				writer.write(author);
			newLine();
			
			writer.write(PROP_START + SEPARATOR);
			if(startTime != null)
				writer.write(startTime.format(FORMATTER));
			newLine();
			
			writer.write(PROP_END + SEPARATOR);
			if(endTime != null)
				writer.write(endTime.format(FORMATTER));
			newLine();
			
			String duration = TimeCalc.createDurationString(startTime, endTime);
			
			writer.write(PROP_DURATION + SEPARATOR);
			writer.write(duration);
			newLine();

			newLine();

			writer.write(PROP_PULSE + SEPARATOR);
			writer.write(Float.toString(pulsePerLitre));
			newLine();
			
			newLine();

			writer.write(PROP_FLOWMETERS + SEPARATOR);
			writer.write(Integer.toString(repository.getNumOfFlowmeters()));
			newLine();

			writer.write(PROP_SIZE + SEPARATOR);
			writer.write(Integer.toString(data.size()));
			newLine();

			newLine();

			writer.write(DATA_HEAD);
			newLine();

//			TODO flowmeters names
//			writer.write(";;;");
//			for(int i=0; i<data.size(); i++)
//				writer.write("flowmeter" + (i+1) + ";");
//			writer.write("\n");
			
			if(options.getColumns().size() > 1) {
				//times
				for(int i=0; i<options.getTimeFormats().size(); i++)
					writer.write(SEPARATOR);
				
				if(options.isFlowmeterValuesTogether())
					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write(DEF_FLOWMETER_NAME + Integer.toString(i+1) + SEPARATOR + SEPARATOR);
				else {

					if(options.getColumns().get(0) == Columns.Pulses)
						writer.write(PULSE_COLUMNNAME);
					else
						writer.write(FLOW_COLUMNNAME);

					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write(SEPARATOR);

					if(options.getColumns().get(0) == Columns.Pulses)
						writer.write(FLOW_COLUMNNAME);
					else
						writer.write(PULSE_COLUMNNAME);

				}
				
				newLine();
			}
			
			if(options.getTimeFormats().contains(TimeFormat.Unix))
				writer.write(UNIX_TIME_HEAD + SEPARATOR);
			if(options.getTimeFormats().contains(TimeFormat.Full))
				writer.write(FULL_TIME_HEAD + SEPARATOR);
			if(options.getTimeFormats().contains(TimeFormat.ProcessTime))
				writer.write(PROCESS_TIME_HEAD + SEPARATOR);

			

			if(options.getColumns().size() > 1) {

				if(options.isFlowmeterValuesTogether()) {
					
					if(options.getColumns().get(0) == Columns.Pulses)
						for(int i=0; i<repository.getNumOfFlowmeters(); i++)
							writer.write(PULSE_COLUMNNAME + SEPARATOR + FLOW_COLUMNNAME + SEPARATOR);
					else
						for(int i=0; i<repository.getNumOfFlowmeters(); i++)
							writer.write(FLOW_COLUMNNAME + SEPARATOR + PULSE_COLUMNNAME + SEPARATOR);
					
				} else {
					
					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write(DEF_FLOWMETER_NAME + Integer.toString(i+1) + SEPARATOR);
					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write(DEF_FLOWMETER_NAME + Integer.toString(i+1) + SEPARATOR);
				}				
			} else {
				for(int i=0; i<repository.getNumOfFlowmeters(); i++) {
					writer.write(DEF_FLOWMETER_NAME + Integer.toString(i+1) + SEPARATOR);
					for(int j=1; j<options.getColumns().size(); j++)
						writer.write(SEPARATOR);
				}
			}
			newLine();
			
			data.forEach(this::action);
				
			writer.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return false;
	}


	private void action(FlowMeasurement measurement) {
		flowConverter.newDataEvent(measurement.getTime());

		try {
			if(options.getTimeFormats().contains(TimeFormat.Unix)) {
				writer.write(Long.toString(measurement.getTime().toEpochSecond(ZoneOffset.UTC)));
				writer.write(SEPARATOR);
			}
			if(options.getTimeFormats().contains(TimeFormat.Full)) {
				writer.write(measurement.getTime().format(FORMATTER));
				writer.write(SEPARATOR);
			}
			if(options.getTimeFormats().contains(TimeFormat.ProcessTime)) {
				writer.write(Long.toString(ChronoUnit.SECONDS.between(startTime, measurement.getTime())));
				writer.write(SEPARATOR);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<Columns> flowColumn = Arrays.asList(Columns.Flow);
		List<Columns> pulsesColumn = Arrays.asList(Columns.Pulses);

		if(options.isFlowmeterValuesTogether() || options.getColumns().size() <= 1)
			writeMeasurment(measurement, options.getColumns());
		else {
			if(options.getColumns().get(0) == Columns.Flow) {
				writeMeasurment(measurement, flowColumn);
				writeMeasurment(measurement, pulsesColumn);
			} else {
				writeMeasurment(measurement, pulsesColumn);
				writeMeasurment(measurement, flowColumn);				
			}
		}
		
		newLine();
	}


	private void writeMeasurment(FlowMeasurement measurement, List<Columns> columns) {
		for(int i=0; i<repository.getNumOfFlowmeters(); i++) {
			int pulses = measurement.get(i);		
			
			
			if(columns.contains(Columns.Pulses) && columns.contains(Columns.Flow))
				if(columns.get(0) == Columns.Pulses) {
					writePulses(pulses);
					writeFlow(pulses);
				} else {
					writeFlow(pulses);
					writePulses(pulses);
				}
			else
			if(columns.contains(Columns.Pulses)) 
				writePulses(pulses);
			else
			if(columns.contains(Columns.Flow)) 
				writeFlow(pulses);
			
					
		}
	}


	private void newLine(){
		try {
			writer.write(NEW_LINE_SEPARATOR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	
	private void writePulses(int pulses) {
		try {
			writer.write(Integer.toString(pulses));
			writer.write(SEPARATOR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void writeFlow(int pulses) {
		Float flow = flowConverter.pulsesToLitrePerSec(pulses);
		try {
			writeFlow(flow);
			writer.write(SEPARATOR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void writeFlow(Float flow) throws IOException {
		if(flow != null) {
			String stringFlow = Float.toString(flow);
			if(options.isCommaSeparator())
				stringFlow = stringFlow.replaceAll("\\.", ",");
			else
				stringFlow = stringFlow.replaceAll(",", ".");
			writer.write(stringFlow);
		}
	}

}
