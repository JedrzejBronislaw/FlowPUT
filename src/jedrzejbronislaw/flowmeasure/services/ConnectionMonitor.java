package jedrzejbronislaw.flowmeasure.services;

import jedrzejbronislaw.flowmeasure.events.EventListener;

public interface ConnectionMonitor extends EventListener {

	void start();
	void stop();
	
	void newMessage();
}
