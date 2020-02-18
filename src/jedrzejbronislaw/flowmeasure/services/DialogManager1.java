package jedrzejbronislaw.flowmeasure.services;

import java.util.HashMap;
import java.util.Map;

public class DialogManager1 implements EventListener{

	static private final int milisecPerLetter = 40;
	
	public interface MessageEvent {
		void show(String title, String content, int closeDelay);
	}
	
	static public class builder{
		private Map<EventType, String> messages = new HashMap<EventListener.EventType, String>();
		private MessageEvent messageEvent;
		
		public builder addMessages(EventType event, String message){
			messages.put(event, message);
			return this;
		}
		
		public builder setShowMessage(MessageEvent messageEvent) {
			this.messageEvent = messageEvent;
			return this;
		}
		
		public DialogManager1 build() {
			if(messages.size() == 0 || messageEvent == null)
				return null;
			else
				return new DialogManager1(messages, messageEvent);
		}
	}

	
	private Map<EventType, String> messages;
	private MessageEvent showMessage;

	
	private DialogManager1(Map<EventType, String> messages, MessageEvent messageEvent) {
		this.messages = messages;
		this.showMessage = messageEvent;
	}
	
	@Override
	public void event(EventType event) {
		String message = messages.get(event);
		if(message != null) 
			showMessage.show(event.toString(), message, time(message));
	}

	private int time(String message) {
		return Math.max(1000, milisecPerLetter * message.length());
	}
	
	
}
