package jedrzejbronislaw.flowmeasure.services;

import jedrzejbronislaw.flowmeasure.states.MultiComparable;

public enum EventType implements MultiComparable {
	Connecting_Start,
	ConnectionSuccessful,
	LostConnection, 
	ConnectionFailed,Diconnection,
	Process_Starts, Process_Ends,
	Calibration_Starts, Calibration_Ends,
	ReceivedData,
	Saving_Process,
	Exiting;
}
