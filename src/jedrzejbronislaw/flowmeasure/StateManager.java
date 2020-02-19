package jedrzejbronislaw.flowmeasure;

import java.util.function.Function;

import jedrzejbronislaw.flowmeasure.services.EventListener;
import jedrzejbronislaw.flowmeasure.services.EventListener.EventType;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.tools.observableState.ObservableState1;
import lombok.Getter;

public class StateManager implements EventListener{
	
	@Getter
	private ObservableState1<ApplicationState> appState = new ObservableState1<>(ApplicationState.Idle);
	@Getter
	private ObservableState1<ConnectionState> connState = new ObservableState1<>(ConnectionState.Disconnected);
	@Getter
	private ObservableState1<ProcessState> processState = new ObservableState1<>(ProcessState.Before);

	@Getter
	private Function<EventType, Boolean> eventPermission = new Function<EventType, Boolean>() {
		
		@Override
		public Boolean apply(EventType event) {
			return (
				(event == EventType.Process_Ends && appState.getState() == ApplicationState.Process) ||
				(event == EventType.Process_Starts && appState.getState() == ApplicationState.Idle && connState.getState() == ConnectionState.Connected) ||
				(event == EventType.LostConnection && connState.getState() == ConnectionState.Connected) ||
				(event == EventType.ConnectionSuccessful && connState.getState() == ConnectionState.Connecting) ||
				(event == EventType.ConnectionFailed && connState.getState() == ConnectionState.Connecting) ||
				(event == EventType.Connecting_Start && connState.getState() == ConnectionState.Disconnected) ||
				(event == EventType.Diconnection && connState.getState() == ConnectionState.Connected) ||
				(event == EventType.Calibration_Starts && appState.getState() == ApplicationState.Idle && connState.getState() == ConnectionState.Connected) ||
				(event == EventType.Calibration_Ends && appState.getState() == ApplicationState.Calibration)
					);
		}
	};
	
	
	@Override
	public void event(EventType event) {

		if(event == EventType.Process_Ends) {
//			if (appState.getState() != ApplicationState.Process) return;

			processState.setState(ProcessState.Finished);
			appState.setState(ApplicationState.Idle);
		}
		
		
		if(event == EventType.Process_Starts) {
//			if (appState.getState() != ApplicationState.Idle) return;

			processState.setState(ProcessState.Ongoing);
			appState.setState(ApplicationState.Process);
		}
		
		
		if(event == EventType.LostConnection) {

			processState.setState(ProcessState.LostConnection);
			appState.setState(ApplicationState.Idle);
			connState.setState(ConnectionState.Disconnected);
		}
		
		if(event == EventType.ConnectionSuccessful) {
			
			connState.setState(ConnectionState.Connected);
		}
		
		if(event == EventType.ConnectionFailed) {
			
			connState.setState(ConnectionState.Disconnected);
		}
		
		if(event == EventType.Connecting_Start) {
			
			connState.setState(ConnectionState.Connecting);
		}
		
		if(event == EventType.Diconnection) {
			
			connState.setState(ConnectionState.Disconnected);
		}
		
		//---
		
		if(event == EventType.Calibration_Starts) {

			appState.setState(ApplicationState.Calibration);
		}
		
		if(event == EventType.Calibration_Ends) {

			appState.setState(ApplicationState.Idle);
		}
	}
	
}
