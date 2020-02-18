package jedrzejbronislaw.flowmeasure.tools.observableState;


public enum State{;
//	ConnectionSuccessful,
//	LostConnection, 
//	ConnectionFailed,
//	Process_Starts, Process_Ends,
//	Calibration_Starts, Calibration_Ends;
	
	public boolean isOneOf(State ...stateTypes) {
		for (int i=0; i<stateTypes.length; i++)
			if (stateTypes[i] == this)
				return true;
		return false;
	}
}