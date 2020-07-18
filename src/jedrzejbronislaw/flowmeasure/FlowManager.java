package jedrzejbronislaw.flowmeasure;

import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class FlowManager {

	public enum FlowConsumerType{
		None,
		Plain,
		Buffered,
		Calibration
	}

	
	@Getter private FlowConsumerType flowConsumerType;
	        private FlowMeasurementConsumer flowConsumer;
	
	        private FlowMeasurementConsumer noneFlowConsumer = pulses -> {};
	@Setter private Supplier<FlowMeasurementConsumer> bufferCreator;
	@Setter private Calibration calibration;

	        private final Repository repository;

	
	public FlowManager(Repository repository) {
		this.repository = repository;
		setFlowConsumerType(FlowConsumerType.None);
	}
	
	public void addFlowMeasurement(int[] pulses) {
		flowConsumer.addFlowMeasurement(pulses);
	}
	
	public void setFlowConsumerType(FlowConsumerType flowConsumerType) {
		this.flowConsumerType = flowConsumerType;
		
		switch (flowConsumerType) {
			case None:        flowConsumer = noneFlowConsumer; break;
			case Plain:       flowConsumer = processRepository(); break;
			case Buffered:    flowConsumer = Injection.get(bufferCreator, noneFlowConsumer); break;
			case Calibration: flowConsumer = calibration(); break;
	
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
