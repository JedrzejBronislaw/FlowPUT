package jedrzejbronislaw.flowmeasure;

import jedrzejbronislaw.flowmeasure.flowDevice.FlowDevice;
import jedrzejbronislaw.flowmeasure.flowDevice.UARTParams;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static jedrzejbronislaw.flowmeasure.ConncetionResult.*;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ConnectionAttempt {

	private static final int CONNECTING_TIMEOUT    = 4000;
	private static final int PROOF_REQUEST_DELAY   = 2000;
	private static final int PROOF_MESSAGE_WAITING = 2000;

	@NonNull
	private FlowDevice device;
	@NonNull
	@Getter
	private UARTParams params;
	
	@Setter
	private Runnable success;
	@Setter
	private Consumer<ConncetionResult> fail;
	
	private ConncetionResult conncetionResult = null;
	
	void changePort(String port) {
		params.PORT_NAME = port;
	}
	
	public void start() {
		conncetionResult = null;
		createCheckingThread().start();
	}

	private Thread createCheckingThread() {
		return new Thread(() -> {
			
			connect(CONNECTING_TIMEOUT);
			
			if(conncetionResult == CONNECTED) {
				sleep(PROOF_MESSAGE_WAITING);
				checkDevice();
			} else
				Injection.run(fail, conncetionResult);
		});
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
		Thread thread = new Thread(() -> conncetionResult = connect());
		
		conncetionResult = DURING;
		thread.setDaemon(true);
		thread.start();
		
		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (conncetionResult == DURING) conncetionResult = NO_RESPONSE;
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
