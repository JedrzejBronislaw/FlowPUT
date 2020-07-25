package jedrzejbronislaw.flowmeasure.components.dialogManager;

import java.util.HashMap;
import java.util.Map;

import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventType;
import lombok.Setter;

public class DialogManager implements EventListener {

	static private final int milisecPerLetter = 60;
	
	public interface MessageEvent {
		void show(String title, String content, int closeDelay);
	}
	
	static public class builder{
		private Map<EventType, String> messages = new HashMap<EventType, String>();
		private MessageEvent messageEvent;
		
		public builder addMessages(EventType event, String message){
			messages.put(event, message);
			return this;
		}
		
		public builder setShowMessage(MessageEvent messageEvent) {
			this.messageEvent = messageEvent;
			return this;
		}
		
		public DialogManager build() {
			return new DialogManager(messages, messageEvent);
		}
	}

	
	        private Map<EventType, String> messages;
	@Setter private MessageEvent showMessage;

	
	private DialogManager(Map<EventType, String> messages, MessageEvent messageEvent) {
		this.messages = messages;
		this.showMessage = messageEvent;
	}
	
	@Override
	public void event(EventType event) {
		if (showMessage == null) return;
		
		String message = messages.get(event);
		if(message != null) 
			showMessage.show(event.toString(), message, time(message));
	}

	private int time(String message) {
		return Math.max(1000, milisecPerLetter * message.length());
	}
}
