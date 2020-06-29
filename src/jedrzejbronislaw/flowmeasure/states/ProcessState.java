package jedrzejbronislaw.flowmeasure.states;

public enum ProcessState implements State {
	Before,
	Ongoing,
	Interrupted,
	LostConnection,
	Finished;
}
