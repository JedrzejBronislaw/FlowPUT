package jedrzejbronislaw.flowmeasure.components.connectionMonitor;

import jedrzejbronislaw.flowmeasure.events.EventListener;

public interface ConnectionMonitor extends EventListener {

	void start();
	void stop();
	
	void newMessage();
}
