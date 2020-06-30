package jedrzejbronislaw.flowmeasure.states;

public enum ProcessState implements MultiComparable {
	Before,
	Ongoing,
	Interrupted,
	LostConnection,
	Finished;
}
