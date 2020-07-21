package jedrzejbronislaw.flowmeasure.flowDevice;

import java.util.function.Consumer;

public interface IUART {
	boolean connect();
	void disconnect();

	boolean send(String message);
	void setReceiveMessage(Consumer<String> messageHandling);

	boolean isPortOpen();
}
