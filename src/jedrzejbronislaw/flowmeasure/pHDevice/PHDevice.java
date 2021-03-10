package jedrzejbronislaw.flowmeasure.pHDevice;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import lombok.Setter;

public class PHDevice extends UARTDevice {

	private static final String VALUE_SEPARATOR = ";";
	private static final char LINE_FIRST_CHAR   = '^';
	private static final char LINE_LAST_CHAR    = '$';
	
	private static final String PROOF_REQUEST = "PHDevice";
	private static final String PROOF_MESSAGE = "pHD present!";


	@Setter private BiConsumer<Integer, Integer> newSingleFlowReceive;
	@Setter private Consumer<int[]> newFlowsReceive;
	

	public PHDevice() {
		super(PROOF_REQUEST, PROOF_MESSAGE);
	}

	@Override
	protected MessageTag handleMessageLine(String message) {
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
}
