package jedrzejbronislaw.flowmeasure.tools.uart.connection;

import static jedrzejbronislaw.flowmeasure.tools.uart.connection.ConncetionResult.CONNECTED;
import static jedrzejbronislaw.flowmeasure.tools.uart.connection.ConncetionResult.DURING;
import static jedrzejbronislaw.flowmeasure.tools.uart.connection.ConncetionResult.NO_RESPONSE;
import static jedrzejbronislaw.flowmeasure.tools.uart.connection.ConncetionResult.WRONG_DEVICE;

import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ConnectionAttempt {

	private static final int CONNECTING_TIMEOUT    = 4000;
	private static final int PROOF_REQUEST_DELAY   = 2000;
	private static final int PROOF_MESSAGE_WAITING = 2000;

	@NonNull         private UARTDevice device;
	@NonNull @Getter private UARTParams params;
	
	@Setter private Runnable success;
	@Setter private Consumer<ConncetionResult> fail;
	
	private ConncetionResult connectionResult = null;
	
	
	void changePort(String port) {
		params.PORT_NAME = port;
	}
	
	public void start() {
		connectionResult = null;
		createCheckingThread().start();
	}

	private Thread createCheckingThread() {
		Thread thread = new Thread(() -> {
			
			connect(CONNECTING_TIMEOUT);
			
			if(connectionResult == CONNECTED) {
				sleep(PROOF_MESSAGE_WAITING);
				checkDevice();
			} else
				Injection.run(fail, connectionResult);
		});
		thread.setDaemon(true);
		
		return thread;
	}

	private void checkDevice() {
		
		if(device.isCorrectDevice()) {
			Injection.run(success);
		} else {
			device.disconnect();
			Injection.run(fail, WRONG_DEVICE);
		}
	}
	
	private void connect(int timeout) {
		Thread thread = new Thread(() -> connectionResult = connect());
		
		connectionResult = DURING;
		thread.setDaemon(true);
		thread.start();
		
		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (connectionResult == DURING) connectionResult = NO_RESPONSE;
	}

	private ConncetionResult connect() {
		ConncetionResult result = device.connect(params);
		
		if(result == CONNECTED) {
			sleep(PROOF_REQUEST_DELAY);
			device.sendProofRequest();
		}
		
		return result;
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
