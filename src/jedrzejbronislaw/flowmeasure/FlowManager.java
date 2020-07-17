package jedrzejbronislaw.flowmeasure;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class FlowManager {

	private final static int FLOWMETERS_NUMBER = 6;

	public enum FlowConsumerType{
		None,
		Plain,
		Buffered,
		Calibration
	}


	private final Repository repository;

	@Getter
	private ProcessRepository currentProcessRepository;

	public ProcessRepository createNewProcessRepository(String name) {
		ProcessRepository processRepository = repository.createNewProcessRepository(FLOWMETERS_NUMBER, name);
		processRepository.getMetadata().setAuthor("unknown");

		plainFlowConsumer = currentProcessRepository = processRepository;
		
		return processRepository;
	}

	
	public FlowManager(Repository repository) {
		this.repository = repository;
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
			flowConsumer = Injection.get(bufferCreator, noneFlowConsumer);
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

	
	@Getter
	private FlowMeasurementConsumer flowConsumer;
	
	private FlowMeasurementConsumer noneFlowConsumer = pulses -> {};
	
	@Setter
	private Calibration calibration;
	
	private ProcessRepository plainFlowConsumer;
	
	@Setter
	private Supplier<FlowMeasurementConsumer> bufferCreator;

}
