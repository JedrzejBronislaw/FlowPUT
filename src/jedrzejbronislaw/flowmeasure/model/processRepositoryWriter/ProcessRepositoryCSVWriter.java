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
	private static final String METADATA_HEAD = "Metadata";
	private static final String DATA_HEAD     = "Measurement";

	private static final String title = "Flow measurement";//"Pomiar przep³ywu";//TODO internationalization
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final String separator = ";";
	
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
			writer = writerCreator.apply(file);
			startTime = repository.getMetadata().getStartTime();
			endTime = repository.getMetadata().getEndTime();
			
			List<FlowMeasurement> data = repository.getAllMeasurementCopy();


			writer.write(title);
			newLine();

			newLine();

			writer.write(METADATA_HEAD);
			newLine();

			writer.write("name" + separator);
			writer.write(repository.getMetadata().getName());
			newLine();
			
			writer.write("author" + separator);
			writer.write(repository.getMetadata().getAuthor());
			newLine();
			
			writer.write("start" + separator);
			if(startTime != null)
				writer.write(startTime.format(formatter));
			newLine();
			
			writer.write("end" + separator);
			if(endTime != null)
				writer.write(endTime.format(formatter));
			newLine();
			
			String duration = TimeCalc.createDurationString(startTime, endTime);
			
			writer.write("duration" + separator);
			writer.write(duration);
			newLine();

			newLine();

			writer.write("pulse per litre" + separator);
			writer.write(Float.toString(pulsePerLitre));
			newLine();
			
			newLine();

			writer.write("num of flowmeters" + separator);
			writer.write(Integer.toString(repository.getNumOfFlowmeters()));
			newLine();

			writer.write("size" + separator);
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
					writer.write(separator);
				
				if(options.isFlowmeterValuesTogether())
					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write("flowmeter" + Integer.toString(i+1) + separator + separator);
				else {

					if(options.getColumns().get(0) == Columns.Pulses)
						writer.write("p");
					else
						writer.write("f");

					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write(separator);

					if(options.getColumns().get(0) == Columns.Pulses)
						writer.write("f");
					else
						writer.write("p");

				}
				
				newLine();
			}
			
			if(options.getTimeFormats().contains(TimeFormat.Unix))
				writer.write("unix time" + separator);
			if(options.getTimeFormats().contains(TimeFormat.Full))
				writer.write("full time" + separator);
			if(options.getTimeFormats().contains(TimeFormat.ProcessTime))
				writer.write("process time [s]" + separator);

			

			if(options.getColumns().size() > 1) {

				if(options.isFlowmeterValuesTogether()) {
					
					if(options.getColumns().get(0) == Columns.Pulses)
						for(int i=0; i<repository.getNumOfFlowmeters(); i++)
							writer.write("p" + separator + "f" + separator);
					else
						for(int i=0; i<repository.getNumOfFlowmeters(); i++)
							writer.write("f" + separator + "p" + separator);
					
				} else {
					
					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write("flowmeter" + Integer.toString(i+1) + separator);
					for(int i=0; i<repository.getNumOfFlowmeters(); i++)
						writer.write("flowmeter" + Integer.toString(i+1) + separator);
				}				
			} else {
				for(int i=0; i<repository.getNumOfFlowmeters(); i++) {
					writer.write("flowmeter" + Integer.toString(i+1) + separator);
					for(int j=1; j<options.getColumns().size(); j++)
						writer.write(separator);
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
				writer.write(separator);
			}
			if(options.getTimeFormats().contains(TimeFormat.Full)) {
				writer.write(measurement.getTime().format(formatter));
				writer.write(separator);
			}
			if(options.getTimeFormats().contains(TimeFormat.ProcessTime)) {
				writer.write(Long.toString(ChronoUnit.SECONDS.between(startTime, measurement.getTime())));
				writer.write(separator);
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
			writer.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	
	private void writePulses(int pulses) {
		try {
			writer.write(Integer.toString(pulses));
			writer.write(separator);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void writeFlow(int pulses) {
		Float flow = flowConverter.pulsesToLitrePerSec(pulses);
		try {
			writeFlow(flow);
			writer.write(separator);
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
