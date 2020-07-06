package jedrzejbronislaw.flowmeasure.events;

import java.util.LinkedList;
import java.util.List;

import lombok.Setter;

public class EventManager {

	@Setter
	private EventPolicy eventPolicy;

	private List<EventListener> listeners = new LinkedList<>();
	
	public void addListener(EventListener newListener) {
		listeners.add(newListener);
	}
	
	public boolean submitEvent(EventType event) {
		
		boolean permission = permission(event);
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
					EventType.Saving_Process,
					EventType.ReceivedData,
					EventType.Exiting));
	}
}
