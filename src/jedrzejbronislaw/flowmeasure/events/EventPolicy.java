package jedrzejbronislaw.flowmeasure.events;

import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.states.StateManager;
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
			(event == EventType.Disconnection        && connState() == ConnectionState.Connected) ||
			(event == EventType.Close_Process        && procState() == ProcessState.Finished) ||
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
	
	private ProcessState procState() {
		return stateManager.getProcessState().getState();
	}
}
