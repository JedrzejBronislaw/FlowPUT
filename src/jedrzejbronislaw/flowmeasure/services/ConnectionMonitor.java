package jedrzejbronislaw.flowmeasure.services;

public interface ConnectionMonitor {

	void start();
	void stop();
	
	void newMessage();
}
