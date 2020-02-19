package jedrzejbronislaw.flowmeasure.states;

public enum ApplicationState{
	Idle, Process, Calibration;
	
	public StateComparator<ApplicationState> compare(){
		return new StateComparator<ApplicationState>(this);
	}
}
