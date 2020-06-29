package jedrzejbronislaw.flowmeasure.states;

public enum ConnectionState implements State {
	Disconnected, Connecting, Connected;
	
	public StateComparator compare(){
		return new StateComparator(this);
	}
}
