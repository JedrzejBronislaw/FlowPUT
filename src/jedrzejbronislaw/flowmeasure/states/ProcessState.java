package jedrzejbronislaw.flowmeasure.states;

import jedrzejbronislaw.flowmeasure.tools.MultiComparable;

public enum ProcessState implements MultiComparable {
	BEFORE,
	ONGOING,
	FINISHED;
}
