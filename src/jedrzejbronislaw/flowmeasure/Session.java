package jedrzejbronislaw.flowmeasure;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.DataBuffer;
import lombok.Getter;
import lombok.Setter;

public class Session {

	public enum FlowConsumerType{
		None,
		Plain,
		Buffered,
		Calibration
	}


	@Setter
	@Getter
	private FlowDevice device;

	@Setter
	@Getter
	private Repository repository;

	@Setter
	@Getter
	private ProcessRepository currentProcessRepository;

	
	public Session() {
		setFlowConsumerType(FlowConsumerType.None);
	}
	
	
	@Getter
	private FlowConsumerType flowConsumerType;
	
	public void setFlowConsumerType(FlowConsumerType flowConsumerType) {
		this.flowConsumerType = flowConsumerType;
		
		switch (flowConsumerType) {
		case None:
			flowConsumer = noneFlowConsumer;
			break;
		case Plain:
			flowConsumer = plainFlowConsumer;
			break;
		case Buffered:
			flowConsumer = bufferedFlowConsumer;
			break;
		case Calibration:
			flowConsumer = calibration;
			break;

		default:
			flowConsumer = noneFlowConsumer;
			break;
		}
	}
	
//----	

	public boolean setBufferInterval(int bufferInterval) {
		if(bufferedFlowConsumer != null) {
			bufferedFlowConsumer.setInterval(bufferInterval);
			return true;
		} else
			return false;
	}
	
	@Getter
	private FlowMeasurementConsumer flowConsumer;
	
	private FlowMeasurementConsumer noneFlowConsumer = pulses -> {};
	
	@Setter
	private Calibration calibration;
	
	@Setter
	private ProcessRepository plainFlowConsumer;

//	@Setter
	private DataBuffer bufferedFlowConsumer;
	
	public void setBufferedFlowConsumer(DataBuffer bufferedFlowConsumer) {
		this.bufferedFlowConsumer = bufferedFlowConsumer;
	}	
	

}
