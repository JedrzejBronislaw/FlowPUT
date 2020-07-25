package jedrzejbronislaw.flowmeasure.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jedrzejbronislaw.flowmeasure.components.flowManager.FlowMeasurementModel;
import lombok.Getter;
import lombok.Setter;

public class ProcessRepository implements FlowMeasurementModel {
	
	@Getter private ProcessMetadata metadata = new ProcessMetadata();
	@Getter private int numOfFlowmeters = 0;
	@Setter private boolean startWithNextValueFlag = false;
	
	private List<FlowMeasurement> measurement = new LinkedList<>();
	
	
	public ProcessRepository(int size, String name) {
		metadata.setName(name);
		
		this.numOfFlowmeters = size;
	}
	
	public void setProcessStartTimeNow() {metadata.setStartTime(LocalDateTime.now());}
	public void setProcessEndTimeNow()   {metadata.setEndTime(  LocalDateTime.now());}
	
	@Override
	public int getSize() {
		return measurement.size();
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		if (startWithNextValueFlag) {
			startWithNextValueFlag = false;
			setProcessStartTimeNow();
		}
		measurement.add(new FlowMeasurement(pulses));
	}

	@Override
	public void addFlowMeasurement(LocalDateTime time, int[] pulses) {
		if (startWithNextValueFlag) {
			startWithNextValueFlag = false;
			metadata.setStartTime(time);
		}
		measurement.add(new FlowMeasurement(time, pulses));
	}
	
	public FlowMeasurement getFlowMeasurement(int index){
		return measurement.get(index);
	}
	
	public List<FlowMeasurement> getAllMeasurement(){
		return Collections.unmodifiableList(measurement);
	}
	
	public void setStartWithNextValueFlag() {
		startWithNextValueFlag = true;
	}
}
