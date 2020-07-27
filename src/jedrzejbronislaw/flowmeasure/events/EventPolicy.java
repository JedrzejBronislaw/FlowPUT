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
			(event == EventType.PROCESS_ENDS          && appState()  == ApplicationState.PROCESS) ||
			(event == EventType.PROCESS_STARTS        && appState()  == ApplicationState.IDLE && connState() == ConnectionState.CONNECTED) ||
			(event == EventType.LOST_CONNECTION       && connState() == ConnectionState.CONNECTED) ||
			(event == EventType.CONNECTION_SUCCESSFUL && connState() == ConnectionState.CONNECTING) ||
			(event == EventType.CONNECTION_FAILED     && connState() == ConnectionState.CONNECTING) ||
			(event == EventType.CONNECTING_START      && connState() == ConnectionState.DISCONNECTED) ||
			(event == EventType.DISCONNECTION         && connState() == ConnectionState.CONNECTED) ||
			(event == EventType.CLOSE_PROCESS         && procState() == ProcessState.FINISHED) ||
			(event == EventType.CALIBRATION_STARTS    && appState()  == ApplicationState.IDLE && connState() == ConnectionState.CONNECTED) ||
			(event == EventType.CALIBRATION_ENDS      && appState()  == ApplicationState.CALIBRATION)
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
