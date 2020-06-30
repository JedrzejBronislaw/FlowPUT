package jedrzejbronislaw.flowmeasure.states;

import jedrzejbronislaw.flowmeasure.tools.MultiComparable;

public enum ConnectionState implements MultiComparable {
	Disconnected, Connecting, Connected;
}
