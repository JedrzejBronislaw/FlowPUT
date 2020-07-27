package jedrzejbronislaw.flowmeasure.states;

import jedrzejbronislaw.flowmeasure.tools.MultiComparable;

public enum ConnectionState implements MultiComparable {
	DISCONNECTED, CONNECTING, CONNECTED;
}
