package jedrzejbronislaw.flowmeasure.devices.flowDevice;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import lombok.Getter;
import lombok.Setter;

public class FlowDevice extends UARTDevice {

	@Getter private String name = "FlowDevice";

	private static final String VALUE_SEPARATOR = ";";
	private static final char LINE_FIRST_CHAR   = '^';
	private static final char LINE_LAST_CHAR    = '$';
	
	private static final String PROOF_REQUEST = "FlowDevice";
	private static final String PROOF_MESSAGE = "FD present!";


	@Setter private BiConsumer<Integer, Integer> newSingleFlowReceive;
	@Setter private Consumer<int[]> newFlowsReceive;
	

	public FlowDevice() {
		super(PROOF_REQUEST, PROOF_MESSAGE);
	}

	@Override
	protected MessageTag handleMessageLine(String message) {
		if (!isLineFormatCorrect(message)) return MessageTag.INCORRECT;

		String content = extraxtContent(message);
		int numbers[]  = extractNumbers(content);
		
		if (numbers == null)         return MessageTag.INCORRECT;
		if (!isSizeCorrect(numbers)) return MessageTag.INCORRECT;
		
		int[] flow = extractFlow(numbers);

		deliverFlow(flow);
		
		return MessageTag.CORRECT;
	}
	
	private void deliverFlow(int[] flow) {
		Injection.run(newFlowsReceive, flow);
		
		if (newSingleFlowReceive != null)
			for (int i=0; i<flow.length; i++) newSingleFlowReceive.accept(flow[i], i);
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
		int n = numbers[0];
		int flows[] = new int[n];
		
		for (int i=0; i<n; i++)
			flows[i] = numbers[i+1];
		
		return flows;
	}
	
	private boolean isSizeCorrect(int[] numbers) {
		return numbers[0] <= numbers.length - 1;
	}

	private boolean isLineFormatCorrect(String message) {
		int length = message.length();
		
		return length >= 4
			&& message.charAt(0) == LINE_FIRST_CHAR
			&& message.charAt(length-1) == LINE_LAST_CHAR;
	}
}
