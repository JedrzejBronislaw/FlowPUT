package jedrzejbronislaw.flowmeasure.states;

public enum ProcessState {
	Before,
	Ongoing,
	Interrupted,
	LostConnection,
	Finished;
	
	public StateComparator<ProcessState> compare(){
		return new StateComparator<ProcessState>(this);
	}
}
