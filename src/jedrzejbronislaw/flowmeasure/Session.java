package jedrzejbronislaw.flowmeasure;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.DataBuffer;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.tools.observableState.ObservableState;
import jedrzejbronislaw.flowmeasure.tools.observableState.ObservableState1;
import lombok.Getter;
import lombok.Setter;

public class Session {

	
	
	public static class Builder{
		private ObservableState1<ApplicationState> appState;
		private ObservableState1<ConnectionState> connState;
		private ObservableState1<ProcessState> processState;
		
		public Builder setAppState(ObservableState1<ApplicationState> appState) {
			this.appState = appState;
			return this;
		}
		public Builder setConnState(ObservableState1<ConnectionState> connState) {
			this.connState = connState;
			return this;
		}
		public Builder setProcessState(ObservableState1<ProcessState> processState) {
			this.processState = processState;
			return this;
		}
		
		public Session build() {
			Session session = new Session();
			session.connState = this.connState;
			session.appState = this.appState;
			session.processState = this.processState;
			
			return session;
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public enum FlowConsumerType{
		None,
		Plain,
		Buffered,
		Calibration
	}




	private ObservableState1<ApplicationState> appState;
	private ObservableState1<ConnectionState> connState;
	private ObservableState1<ProcessState> processState;
	
	
	@Setter
	@Getter
	private FlowDevice device;

	@Setter
	@Getter
	private Repository repository;

	@Setter
	@Getter
	private ProcessRepository currentProcessRepository;

	
	private Session() {
		setFlowConsumerType(FlowConsumerType.None);
	}
	
	public ObservableState<ApplicationState> getAppState() {
		return appState;
	}
	public ObservableState<ConnectionState> getConnState() {
		return connState;
	}
	public ObservableState<ProcessState> getProcessState() {
		return processState;
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
