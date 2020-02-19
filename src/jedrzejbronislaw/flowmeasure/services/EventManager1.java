package jedrzejbronislaw.flowmeasure.services;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import jedrzejbronislaw.flowmeasure.services.EventListener.EventType;
import lombok.Setter;

public class EventManager1{

	@Setter
	private Function<EventType, Boolean> checkPermission;

	private List<EventListener> listeners = new LinkedList<>();
	
	public void addListener(EventListener newListener) {
		listeners.add(newListener);
	}
	
	public boolean event(EventType event) {
		
		boolean permission = permission(event);
		if(permission)
			listeners.forEach(listener -> listener.event(event));
		
		return permission;
	}
	
	private boolean permission(EventType event){
		return (checkInternalPermission(event) ||
				(checkPermission != null && checkPermission.apply(event)));
	}
	
	private boolean checkInternalPermission(EventType event){
		return (event.isOneOf(
					EventType.Saving_Process,
					EventType.ReceivedData,
					EventType.Exiting));
	}
}
