package jedrzejbronislaw.flowmeasure.flowDevice;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class FlowDevice {

	private enum MessageTag {
		CORRECT, INCORRECT, IGNORED
	}
	
	private static final String LINE_SEPARATOR_REGEX = "\\r?\\n";
	private static final String VALUE_SEPARATOR = ";";
	private static final char LINE_FIRST_CHAR   = '^';
	private static final char LINE_LAST_CHAR    = '$';
	
	private static final String PROOF_REQUEST = "FlowDevice";
	private static final String PROOF_MESSAGE = "FD present!";

	private IUART uart;

	@Setter private BiConsumer<Integer, Integer> newSingleFlowReceive;
	@Setter private Consumer<int[]> newFlowsReceive;
	@Setter private Consumer<String> incorrectMessageReceive;
	@Setter private Runnable deviceConfirmation;
	@Setter private Function<UARTParams, IUART> uartGenerator = UART::new;
	
	@Getter private boolean connected     = false;
	@Getter private boolean correctDevice = false;
	
	private boolean connecting = false;
	

	public FlowDevice() {}
	public FlowDevice(Function<UARTParams, IUART> uartGenerator) {
		if (uartGenerator != null) this.uartGenerator = uartGenerator;
	}
	
	
	public boolean connect(UARTParams params) {
		if(connecting) return false;
		connecting = true;
		
		uart = uartGenerator.apply(params);
		uart.setReceiveMessage(message -> handleMessage(message));
		connected = uart.connect();
		
		connecting = false;
		
		return connected;
	}

	public void disconnect() {
		if(uart == null || !uart.isPortOpen()) return;
		
		uart.disconnect();
		connected = false;
		correctDevice = false;
	}

	public void sendProofRequest() {
		uart.send(PROOF_REQUEST);
	}

	private void handleMessage(String messageLines) {
		if(messageLines == null || messageLines.isEmpty()) return;
		
		String[] messages = messageLines.split(LINE_SEPARATOR_REGEX);
		
		Stream.of(messages).filter(this::unexecuted).forEach(message -> Injection.run(incorrectMessageReceive, message));
	}
	
	private boolean unexecuted(String message) {
		return handleMessageLine(message) == MessageTag.INCORRECT;
	}

	private MessageTag handleMessageLine(String message) {
		if(isProofMessage(message))     return MessageTag.CORRECT;
		if(!correctDevice)              return MessageTag.IGNORED;
		if(!correctLineFormat(message)) return MessageTag.INCORRECT;

		String content = extraxtContent(message);
		int numbers[]  = extractNumbers(content);
		
		if(numbers == null)             return MessageTag.INCORRECT;
		if(correctSize(numbers))        return MessageTag.INCORRECT;
		
		int[] flow = extractFlow(numbers);

		deliverFlow(flow);
		
		return MessageTag.CORRECT;
	}
	
	private void deliverFlow(int[] flow) {
		Injection.run(newFlowsReceive, flow);
		
		if (newSingleFlowReceive != null)
			for(int i=0; i<flow.length; i++) newSingleFlowReceive.accept(flow[i], i);
	}

	private int[] extractNumbers(String message) {
		String strNumbers[] = message.split(VALUE_SEPARATOR);
		
		try {
			return Stream.of(strNumbers).mapToInt(Integer::parseInt).toArray();
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private String extraxtContent(String message) {
		return message.substring(1, message.length()-1);
	}
	
	private int[] extractFlow(int[] numbers) {
		return IntStream.rangeClosed(1, numbers[0]).map(i -> numbers[i]).toArray();
	}
	
	private boolean correctSize(int[] numbers) {
		return numbers[0]+1 > numbers.length;
	}

	private boolean correctLineFormat(String message) {
		int length = message.length();
		
		return length >= 4
			&& message.charAt(0) == LINE_FIRST_CHAR
			&& message.charAt(length-1) == LINE_LAST_CHAR;
		
	}

	private boolean isProofMessage(String message) {
		if (!message.equals(PROOF_MESSAGE)) return false;
		
		correctDevice = true;
		Injection.run(deviceConfirmation);
		
		return true;
	}
}
