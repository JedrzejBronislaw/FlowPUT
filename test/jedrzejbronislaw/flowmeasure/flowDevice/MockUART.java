package jedrzejbronislaw.flowmeasure.flowDevice;

import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class MockUART implements IUART {

	@Setter private Consumer<String> receiveMessage;
	
	public void createMessage(String message) {
		Injection.run(receiveMessage, message);
	}
	
	@Override public boolean connect() {return false;}
	@Override public void disconnect() {}
	@Override public boolean send(String message) {return false;}
	@Override public boolean isPortOpen() {return false;}
}
