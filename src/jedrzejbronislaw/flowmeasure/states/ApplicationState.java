package jedrzejbronislaw.flowmeasure.states;

public enum ApplicationState implements State{
	Idle, Process, Calibration;
	
	public StateComparator compare(){
		return new StateComparator(this);
	}
}
