package jedrzejbronislaw.flowmeasure.events;

import jedrzejbronislaw.flowmeasure.tools.MultiComparable;

public enum EventType implements MultiComparable {
	CONNECTING_START,
	CONNECTION_SUCCESSFUL,
	LOST_CONNECTION,
	CONNECTION_FAILED, DISCONNECTION,
	PROCESS_STARTS, PROCESS_ENDS,
	CALIBRATION_STARTS, CALIBRATION_ENDS,
	RECEIVED_DATA,
	SAVING_PROCESS, CLOSE_PROCESS,
	EXITING;
}
