package jedrzejbronislaw.flowmeasure.states;

import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import lombok.Getter;
import lombok.NonNull;

public class AllStates {

	private StateListener<ApplicationState> appStateListener = this::setAppState;
	private StateListener<ConnectionState> connStateListener = this::setConnState;
	private StateListener<ProcessState>    procStateListener = this::setProcState;
	
	@NonNull private AllStatesListener listener;
	
	@Getter private ApplicationState appState;
	@Getter private ConnectionState  connState;
	@Getter private ProcessState     procState;
	
	public AllStates(StateManager stateManager, AllStatesListener listener) {
		this.listener = listener;
		
		stateManager.getAppState()    .addStateListener(appStateListener);
		stateManager.getConnState()   .addStateListener(connStateListener);
		stateManager.getProcessState().addStateListener(procStateListener);
	}
	
	public boolean is(ApplicationState applicationState, ConnectionState connectionState, ProcessState processState) {
		return (appState  == null || appState  == applicationState) &&
		       (connState == null || connState == connectionState)  &&
		       (procState == null || procState == processState);
	}
	
	public boolean is(ApplicationState... state) {
		return appState.isOneOf(state);
	}
	
	public boolean is(ConnectionState... state) {
		return connState.isOneOf(state);
	}
	
	public boolean is(ProcessState... state) {
		return procState.isOneOf(state);
	}
	
	
	public boolean is(ApplicationState state) {
		return appState == state;
	}
	
	public boolean is(ConnectionState state) {
		return connState == state;
	}
	
	public boolean is(ProcessState state) {
		return procState == state;
	}
	
	
	private void setAppState(ApplicationState appState) {
		this.appState = appState;
		action();
	}
	
	private void setConnState(ConnectionState connState) {
		this.connState = connState;
		action();
	}
	
	private void setProcState(ProcessState procState) {
		this.procState = procState;
		action();
	}
	
	
	private void action() {
		listener.onChangeState(this);
	}
}
