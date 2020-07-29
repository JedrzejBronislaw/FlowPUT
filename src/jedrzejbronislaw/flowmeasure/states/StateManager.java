package jedrzejbronislaw.flowmeasure.states;

import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.tools.observableState.ObservableState1;
import lombok.Getter;

public class StateManager implements EventListener {
	
	@Getter private ObservableState1<ApplicationState> appState = new ObservableState1<>(ApplicationState.IDLE);
	@Getter private ObservableState1<ConnectionState> connState = new ObservableState1<>(ConnectionState.DISCONNECTED);
	@Getter private ObservableState1<ProcessState> processState = new ObservableState1<>(ProcessState.BEFORE);
	
	
	@Override
	public void event(EventType event) {

		if(event == EventType.PROCESS_ENDS) {
			processState.setState(ProcessState.FINISHED);
		} else
		
		
		if(event == EventType.PROCESS_STARTS) {
			processState.setState(ProcessState.ONGOING);
			appState.setState(ApplicationState.PROCESS);
		} else
		
		
		if(event == EventType.LOST_CONNECTION ||
		   event == EventType.DISCONNECTION) {
			if(processState.is(ProcessState.ONGOING))
				processState.setState(ProcessState.FINISHED); else
				appState.setState(ApplicationState.IDLE);
			connState.setState(ConnectionState.DISCONNECTED);
		} else
			
			
		if(event == EventType.CLOSE_PROCESS) {
			processState.setState(ProcessState.BEFORE);
			appState.setState(ApplicationState.IDLE);
		} else
		
		if(event == EventType.CONNECTION_SUCCESSFUL) {
			connState.setState(ConnectionState.CONNECTED);
		} else
		
		if(event == EventType.CONNECTION_FAILED) {
			connState.setState(ConnectionState.DISCONNECTED);
		} else
		
		if(event == EventType.CONNECTING_START) {
			connState.setState(ConnectionState.CONNECTING);
		} else
		
		//---
		
		if(event == EventType.CALIBRATION_STARTS) {
			appState.setState(ApplicationState.CALIBRATION);
		} else
		
		if(event == EventType.CALIBRATION_ENDS) {
			appState.setState(ApplicationState.IDLE);
		}
	}
}
