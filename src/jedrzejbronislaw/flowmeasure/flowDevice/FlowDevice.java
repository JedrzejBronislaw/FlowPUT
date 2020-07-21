package jedrzejbronislaw.flowmeasure.flowDevice;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.application.Platform;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class FlowDevice {

	private static final String PROOF_REQUEST = "FlowDevice";
	private static final String PROOF_MESSAGE = "FD present!";

	private UART uart;

	@Setter
	private BiConsumer<Integer, Integer> newSingleFlowReceive;
	@Setter
	private Consumer<int[]> newFlowsReceive;
	@Setter
	private Consumer<String> IncorrectMessageReceive;
	@Setter
	private Runnable deviceConfirmation;
	
	@Getter
	private boolean connected = false;
	@Getter
	private boolean correctDevice = false;
	
	private boolean connecting = false;
	
	public boolean connect(UARTParams params) {
		if(!connecting) {
			connecting = true;
			
			uart = new UART(params);
			uart.setReceiveMessage(message -> parse(message));
			connected = uart.connect();
				
			connecting = false;
			return connected;
		} else
			return false;
	}

	public void sendProofRequest() {
		uart.send(PROOF_REQUEST);
	}

	private void parse(String message) {
		if(message.isEmpty()) return;
		
		String[] lines = message.split("\\r?\\n");
		
		for(int i=0; i<lines.length; i++)
			parseLine(lines[i]);
	}

	private void parseLine(String message) {
		int position;
		int length;
		int flow[];
		
		if(checkProofMessage(message)) return;
		
		if(!correctDevice) return;
		
		length = message.length();
		if (length <4) {
			Injection.run(IncorrectMessageReceive, message);
			return;
		}
		
		if(message.charAt(0) != '^' || message.charAt(length-1) != '$') {
			Injection.run(IncorrectMessageReceive, message);
			return;			
		}
		
		message = message.substring(1);
		
		
		position = message.indexOf(';');
		if(position == -1) {
			Injection.run(IncorrectMessageReceive, message);
			return;			
		}
		
		int n = Integer.parseInt(message.substring(0, position));
		
		flow = new int[n];
		
		
		for(int i=0; i<n; i++) {			
			message = message.substring(position+1);
			
			position = message.indexOf(';');
			if(position == -1) {
				Injection.run(IncorrectMessageReceive, message);
				return;			
			}
			
			flow[i] = Integer.parseInt(message.substring(0, position));
		}
		
		
		Injection.run(newFlowsReceive, flow);
		
		for(int i=0; i<n; i++)
			if (newSingleFlowReceive != null) {
				int ii = i;
				Platform.runLater(() -> newSingleFlowReceive.accept(flow[ii], ii));
			}
	}

	private boolean checkProofMessage(String message) {
		if (message.equals(PROOF_MESSAGE)) {
			correctDevice = true;
			Injection.run(deviceConfirmation);
			
			return true;
		}
		return false;
	}

	public void disconnect() {
		if(uart != null && uart.isPortOpen()) {
			uart.disconnect();
			connected = false;
			correctDevice = false;
		}
	}

}
