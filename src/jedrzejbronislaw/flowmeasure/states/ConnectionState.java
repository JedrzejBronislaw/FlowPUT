package jedrzejbronislaw.flowmeasure.states;

public enum ConnectionState {
	Disconnected, Connecting, Connected;
	
	public StateComparator<ConnectionState> compare(){
		return new StateComparator<ConnectionState>(this);
	}
}
