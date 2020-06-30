package jedrzejbronislaw.flowmeasure;

import jedrzejbronislaw.flowmeasure.services.EventListener.EventType;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventPolicy {
	
	private final StateManager stateManager;
	
	public boolean checkPermmision(EventType event) {
		return (
			(event == EventType.Process_Ends         && appState()  == ApplicationState.Process) ||
			(event == EventType.Process_Starts       && appState()  == ApplicationState.Idle && connState() == ConnectionState.Connected) ||
			(event == EventType.LostConnection       && connState() == ConnectionState.Connected) ||
			(event == EventType.ConnectionSuccessful && connState() == ConnectionState.Connecting) ||
			(event == EventType.ConnectionFailed     && connState() == ConnectionState.Connecting) ||
			(event == EventType.Connecting_Start     && connState() == ConnectionState.Disconnected) ||
			(event == EventType.Diconnection         && connState() == ConnectionState.Connected) ||
			(event == EventType.Calibration_Starts   && appState()  == ApplicationState.Idle && connState() == ConnectionState.Connected) ||
			(event == EventType.Calibration_Ends     && appState()  == ApplicationState.Calibration)
		);
	}
	
	private ApplicationState appState() {
		return stateManager.getAppState().getState();
	}
	
	private ConnectionState connState() {
		return stateManager.getConnState().getState();
	}
}
