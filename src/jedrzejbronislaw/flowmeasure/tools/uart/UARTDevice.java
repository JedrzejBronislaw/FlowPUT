package jedrzejbronislaw.flowmeasure.tools.uart;

import static jedrzejbronislaw.flowmeasure.tools.uart.connection.ConnectionResult.BUSY;
import static jedrzejbronislaw.flowmeasure.tools.uart.connection.ConnectionResult.CONNECTED;
import static jedrzejbronislaw.flowmeasure.tools.uart.connection.ConnectionResult.CONNECTION_ERROR;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.ConnectionResult;
import lombok.Getter;
import lombok.Setter;

public abstract class UARTDevice {

	protected enum MessageTag {
		CORRECT, INCORRECT, IGNORED
	}
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String LINE_SEPARATOR_REGEX = "\\r?\\n";
	
	protected final String PROOF_REQUEST;
	protected final String PROOF_MESSAGE;

	private IUART uart;

	@Setter private Consumer<String> incorrectMessageReceive;
	@Setter private Runnable deviceConfirmation;
	@Setter private Function<UARTParams, IUART> uartGenerator = UART::new;
	
	@Getter private boolean connected     = false;
	@Getter private boolean correctDevice = false;
	
	private boolean connecting = false;

	protected abstract MessageTag handleMessageLine(String message);
	public abstract String getName();
	@Setter private Consumer<String[]> dataReceiver;
	
	
	public UARTDevice(String proofRequest, String proofMessage) {
		PROOF_REQUEST = proofRequest;
		PROOF_MESSAGE = proofMessage;
	}


	public ConnectionResult connect(UARTParams params) {
		if (connecting) return BUSY;
		connecting = true;
		
		uart = uartGenerator.apply(params);
		uart.setReceiveMessage(message -> handleMessage(message));
		connected = uart.connect();
		
		connecting = false;
		
		return connected ? CONNECTED : CONNECTION_ERROR;
	}

	public void disconnect() {
		if (uart != null && uart.isPortOpen())
			uart.disconnect();

		connected = false;
		correctDevice = false;
	}

	public void sendProofRequest() {
		uart.send(PROOF_REQUEST);
	}

	private void handleMessage(String messageLines) {
		if (messageLines == null || messageLines.isEmpty()) return;
		
		String[] messages = messageLines.split(LINE_SEPARATOR_REGEX);
		
		Stream.of(messages).filter(this::unexecuted).forEach(message -> {
			log.warn("Incorrect Message: {}", message);
			Injection.run(incorrectMessageReceive, message);
		});
	}
	
	private boolean unexecuted(String message) {
		return handleLine(message) == MessageTag.INCORRECT;
	}

	private MessageTag handleLine(String message) {
		if (isProofMessage(message)) return MessageTag.CORRECT;
		if (!correctDevice)          return MessageTag.IGNORED;
		
		return handleMessageLine(message);
	}

	private boolean isProofMessage(String message) {
		if (!message.equals(PROOF_MESSAGE)) return false;
		
		correctDevice = true;
		
		log.info("Device confirmation");
		Injection.run(deviceConfirmation);
		
		return true;
	}
	
	protected void sendToDataReceiver(int[] data) {
		if (dataReceiver == null) return;
		
		String[] strData = new String[data.length];
		
		for (int i=0; i<data.length; i++)
			strData[i] = Integer.toString(data[i]);

		dataReceiver.accept(strData);
	}
}
