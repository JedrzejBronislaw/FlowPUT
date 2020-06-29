package jedrzejbronislaw.flowmeasure.states;

public enum ProcessState implements State {
	Before,
	Ongoing,
	Interrupted,
	LostConnection,
	Finished;
	
	public StateComparator compare(){
		return new StateComparator(this);
	}
}
