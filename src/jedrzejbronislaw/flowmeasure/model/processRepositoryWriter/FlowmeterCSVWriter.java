package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.io.IOException;
import java.time.LocalDateTime;

import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.FlowConverter1;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;
import jedrzejbronislaw.flowmeasure.tools.NumberTools;

public class FlowmeterCSVWriter {

	private FlowConverter flowConverter;
	private CSVWriter writer;
	private int flowmeter;
	private ProcessRepositoryWriterOptions options;

	public FlowmeterCSVWriter(CSVWriter writer, int flowmeter, float pulsePerLitre, ProcessRepositoryWriterOptions options) {
		this.writer = writer;
		this.flowmeter = flowmeter;
		this.options = options;
		flowConverter = new FlowConverter1(pulsePerLitre);
	}
	
	public void writeMeasurement(Unit unit, int pulses) throws IOException {
		if (unit == Unit.Pulses) writePulses(pulses);
		if (unit == Unit.Flow)   writeFlow(pulses);
	}
	
	public void writeMeasurement(Unit unit, FlowMeasurement measurement) throws IOException {
		if (unit == Unit.Pulses) writePulses(measurement.get(flowmeter));
		if (unit == Unit.Flow)   writeFlow(measurement.get(flowmeter));
	}
	
	private void writePulses(int pulses) throws IOException {
		writer.writeWithSeparator(pulses);
	}
	
	private void writeFlow(int pulses) throws IOException {
		Float flow = flowConverter.pulsesToLitrePerSec(pulses);
		writer.writeWithSeparator(setDecimalSeparator(flow));
	}

	private String setDecimalSeparator(Float flow) {
		if(flow == null) return "";
		
		return NumberTools.floatToString(flow, options.getDecimalSeparator().toString());
	}
	
	public void newDataEvent(LocalDateTime time) {
		flowConverter.newDataEvent(time);
	}
}
