package jedrzejbronislaw.flowmeasure.events;

import jedrzejbronislaw.flowmeasure.tools.MultiComparable;

public enum EventType implements MultiComparable {
	Connecting_Start,
	ConnectionSuccessful,
	LostConnection, 
	ConnectionFailed,Diconnection,
	Process_Starts, Process_Ends,
	Calibration_Starts, Calibration_Ends,
	ReceivedData,
	Saving_Process, Close_Process,
	Exiting;
}
