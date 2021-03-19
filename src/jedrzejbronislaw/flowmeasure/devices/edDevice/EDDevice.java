package jedrzejbronislaw.flowmeasure.devices.edDevice;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jedrzejbronislaw.flowmeasure.components.ValueConverter;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import lombok.Getter;
import lombok.Setter;

public class EDDevice extends UARTDevice {

	@Getter private String name = "EDDevice";
	
	private static final String VALUE_SEPARATOR = ";";
	private static final char LINE_FIRST_CHAR   = '^';
	private static final char LINE_LAST_CHAR    = '$';
	
	private static final String PROOF_REQUEST = "EDDevice";
	private static final String PROOF_MESSAGE = "EDD present!";


	@Setter private BiConsumer<Integer, Integer> newSingleFlowReceive;
	@Setter private Consumer<int[]> newFlowsReceive;
	

	public EDDevice() {
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
		Injection.run(newFlowsReceive, convertValues(flow));
		
		if (newSingleFlowReceive != null)
			for(int i=0; i<flow.length; i++) newSingleFlowReceive.accept(flow[i], i);
	}

	private int[] convertValues(int[] values) {
		int[] tab = Arrays.copyOf(values, values.length);
		
		float ph      = ValueConverter.valueToPH(     tab[0]/10f);
		float ec      = ValueConverter.valueToVoltage(tab[1]/10f);
		float ampere  = ValueConverter.valueToAmpere( tab[2]/10f);
		
		tab[0] = (int) (ph     * 100);
		tab[1] = (int) (ec     * 1000);
		tab[2] = (int) (ampere * 1000);
		
		return tab;
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
