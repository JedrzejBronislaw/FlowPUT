package jedrzejbronislaw.flowmeasure.components.flowManager;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.components.calibration.Calibration;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class FlowManager {

	public enum FlowConsumerType {
		NONE,
		PLAIN,
		BUFFERED,
		CALIBRATION
	}

	
	@Getter private FlowConsumerType flowConsumerType;
	        private FlowMeasurementConsumer flowConsumer;
	
	        private FlowMeasurementConsumer noneFlowConsumer = pulses -> {};
	@Setter private Supplier<FlowMeasurementConsumer> bufferCreator;
	@Setter private Calibration calibration;

	        private final Repository repository;

	
	public FlowManager(Repository repository) {
		this.repository = repository;
		setFlowConsumerType(FlowConsumerType.NONE);
	}
	
	public void addFlowMeasurement(int[] pulses) {
		flowConsumer.addFlowMeasurement(pulses);
	}
	
	public void setFlowConsumerType(FlowConsumerType flowConsumerType) {
		this.flowConsumerType = flowConsumerType;
		
		switch (flowConsumerType) {
			case NONE:        flowConsumer = noneFlowConsumer; break;
			case PLAIN:       flowConsumer = processRepository(); break;
			case BUFFERED:    flowConsumer = Injection.get(bufferCreator, noneFlowConsumer); break;
			case CALIBRATION: flowConsumer = calibration(); break;
	
			default:          flowConsumer = noneFlowConsumer; break;
		}
	}

	private FlowMeasurementConsumer calibration() {
		return calibration != null ? calibration : noneFlowConsumer;
	}
	
	private FlowMeasurementConsumer processRepository() {
		ProcessRepository processRepository = repository.getCurrentProcessRepository();
		return processRepository != null ? processRepository : noneFlowConsumer;
	}
}
