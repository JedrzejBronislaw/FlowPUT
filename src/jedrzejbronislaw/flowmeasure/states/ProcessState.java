package jedrzejbronislaw.flowmeasure.states;

import jedrzejbronislaw.flowmeasure.tools.MultiComparable;

public enum ProcessState implements MultiComparable {
	Before,
	Ongoing,
	LostConnection,
	Finished;
}
