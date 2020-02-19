package jedrzejbronislaw.flowmeasure.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import jedrzejbronislaw.flowmeasure.FlowMeasurementConsumer;
import lombok.Getter;

public class ProcessRepository implements FlowMeasurementConsumer{
	
	@Getter
	private ProcessMetadata metadata = new ProcessMetadata();
	
	private List<FlowMeasurement> measurement = new LinkedList<>();
	
	
	@Getter
	private int size = 0;
	
	public void setProcessStartTimeNow() {metadata.setStartTime(LocalDateTime.now());}
	public void setProcessEndTimeNow() {metadata.setEndTime(LocalDateTime.now());}
	
	private boolean setProcessStartTimeWithNextValueFlag = false;
	
	public ProcessRepository(int size, String name) {
		metadata.setName(name);
		
		this.size = size;
	}
	
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		if(setProcessStartTimeWithNextValueFlag) {
			setProcessStartTimeWithNextValueFlag = false;
			setProcessStartTimeNow();
		}
		measurement.add(new FlowMeasurement(pulses));
	}
	
	public void addFlowMeasurement(LocalDateTime time, int[] pulses) {
		if(setProcessStartTimeWithNextValueFlag) {
			setProcessStartTimeWithNextValueFlag = false;
			metadata.setStartTime(time);
		}
		measurement.add(new FlowMeasurement(time, pulses));
	}
	
	public FlowMeasurement getFlowMeasurement(int index){
		return measurement.get(index);
	}
	
	public List<FlowMeasurement> getAllMeasurementCopy(){
		return new LinkedList<>(measurement);
	}
	public void setProcessStartTimeWithNextValue() {
		setProcessStartTimeWithNextValueFlag = true;
	}
	
}
