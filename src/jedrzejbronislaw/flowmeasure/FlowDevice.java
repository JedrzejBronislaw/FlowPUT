package jedrzejbronislaw.flowmeasure;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.application.Platform;
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
//	@Setter
//	private BiConsumer<Integer, Integer> newFlowReceive;
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
		
//		sendProofRequest();
		
			connecting = false;
			return connected;
		} else
			return false;
	}

//	public void write(String mess) {
//		uart.send(mess);
//	}

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
		int pos1, pos2;
		int length;
		int flow[];
		
		if(checkProofMessage(message)) return;
		
		if(!correctDevice) return;
		
		length = message.length();
		if (length <4) {
			if(IncorrectMessageReceive != null)
				IncorrectMessageReceive.accept(message);
			return;
		}
		
		if(message.charAt(0) != '^' || message.charAt(length-1) != '$') {
			if(IncorrectMessageReceive != null)
				IncorrectMessageReceive.accept(message);
			return;			
		}
		
		message = message.substring(1);
		
		
		pos2 = message.indexOf(';');//, pos1);
		if(pos2 == -1) {
			if(IncorrectMessageReceive != null)
				IncorrectMessageReceive.accept(message);
			return;			
		}
		
		int n = Integer.parseInt(message.substring(0, pos2));
		
		flow = new int[n];
		
		
		for(int i=0; i<n; i++) {
//			pos1 = 1;
			
		message = message.substring(pos2+1);
			
			pos2 = message.indexOf(';');//, pos1);
			if(pos2 == -1) {
				if(IncorrectMessageReceive != null)
					IncorrectMessageReceive.accept(message);
				return;			
			}
			
//			if(pos1 == pos2) {
//				if(IncorrectMessageReceive != null)
//					IncorrectMessageReceive.accept(message);
//				return;			
//			}
			
			flow[i] = Integer.parseInt(message.substring(0, pos2));
//			System.out.println(i + ": " + flow[i]);
//			System.out.println();
//			if (newFlowReceive != null) {
//				Platform.runLater(() -> newFlowReceive.accept(flow[i], i));
//			}
		}
		
		
		if(newFlowsReceive != null)
			newFlowsReceive.accept(flow);
		
		for(int i=0; i<n; i++)
			if (newSingleFlowReceive != null) {
				int ii = i;
				Platform.runLater(() -> newSingleFlowReceive.accept(flow[ii], ii));
			}
		

		
//		System.out.println("|" + message.substring(pos1+1, pos2) + "|");
//		System.out.println(pos1 + " -> " + pos2);
	}

	private boolean checkProofMessage(String message) {
		if (message.equals(PROOF_MESSAGE)) {
			correctDevice = true;
			if(deviceConfirmation != null)
				deviceConfirmation.run();
			
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
