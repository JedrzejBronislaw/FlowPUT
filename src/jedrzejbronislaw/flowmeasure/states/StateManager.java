package jedrzejbronislaw.flowmeasure.states;

import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.tools.observableState.ObservableState1;
import lombok.Getter;

public class StateManager implements EventListener {
	
	@Getter
	private ObservableState1<ApplicationState> appState = new ObservableState1<>(ApplicationState.Idle);
	@Getter
	private ObservableState1<ConnectionState> connState = new ObservableState1<>(ConnectionState.Disconnected);
	@Getter
	private ObservableState1<ProcessState> processState = new ObservableState1<>(ProcessState.Before);
	
	
	@Override
	public void event(EventType event) {

		if(event == EventType.Process_Ends) {
			processState.setState(ProcessState.Finished);
			appState.setState(ApplicationState.Idle);
		} else
		
		
		if(event == EventType.Process_Starts) {
			processState.setState(ProcessState.Ongoing);
			appState.setState(ApplicationState.Process);
		} else
		
		
		if(event == EventType.LostConnection) {
			processState.setState(ProcessState.LostConnection);
			appState.setState(ApplicationState.Idle);
			connState.setState(ConnectionState.Disconnected);
		} else
			
			
		if(event == EventType.Close_Process) {
			processState.setState(ProcessState.Before);
		} else
		
		if(event == EventType.ConnectionSuccessful) {
			connState.setState(ConnectionState.Connected);
		} else
		
		if(event == EventType.ConnectionFailed) {
			connState.setState(ConnectionState.Disconnected);
		} else
		
		if(event == EventType.Connecting_Start) {
			connState.setState(ConnectionState.Connecting);
		} else
		
		if(event == EventType.Diconnection) {
			connState.setState(ConnectionState.Disconnected);
		} else
		
		//---
		
		if(event == EventType.Calibration_Starts) {
			appState.setState(ApplicationState.Calibration);
		} else
		
		if(event == EventType.Calibration_Ends) {
			appState.setState(ApplicationState.Idle);
		}
	}
}
