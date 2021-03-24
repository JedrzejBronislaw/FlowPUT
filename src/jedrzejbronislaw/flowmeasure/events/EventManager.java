package jedrzejbronislaw.flowmeasure.events;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Setter;

public class EventManager {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Setter
	private EventPolicy eventPolicy;

	private List<EventListener> listeners = new LinkedList<>();
	
	
	public void addListener(EventListener newListener) {
		listeners.add(newListener);
	}
	
	public boolean submitEvent(EventType event) {
		boolean permission = permission(event);
		if (event != EventType.RECEIVED_DATA) log.info("-> Event: {} ({})", event, permission);
		
		if(permission)
			listeners.forEach(listener -> listener.event(event));
		
		return permission;
	}
	
	private boolean permission(EventType event){
		return (checkInternalPermission(event) ||
				(eventPolicy != null && eventPolicy.checkPermmision(event)));
	}
	
	private boolean checkInternalPermission(EventType event){
		return (event.isOneOf(
					EventType.SAVING_PROCESS,
					EventType.RECEIVED_DATA,
					EventType.EXITING));
	}
}
