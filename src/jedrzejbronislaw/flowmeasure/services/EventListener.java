package jedrzejbronislaw.flowmeasure.services;

public interface EventListener {
	
	enum EventType{
		Connecting_Start,
		ConnectionSuccessful,
		LostConnection, 
		ConnectionFailed,Diconnection,
		Process_Starts, Process_Ends,
		Calibration_Starts, Calibration_Ends,
		ReceivedData,
		Saving_Process,
		Exiting;
		
		public boolean isOneOf(EventType ...eventTypes) {
			for (int i=0; i<eventTypes.length; i++)
				if (eventTypes[i] == this)
					return true;
			return false;
		}
	}
	
	void event(EventType event);

	
}
