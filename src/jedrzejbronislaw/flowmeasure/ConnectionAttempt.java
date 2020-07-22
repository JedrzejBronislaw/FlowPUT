package jedrzejbronislaw.flowmeasure;

import jedrzejbronislaw.flowmeasure.flowDevice.FlowDevice;
import jedrzejbronislaw.flowmeasure.flowDevice.UARTParams;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static jedrzejbronislaw.flowmeasure.ConncetionResult.*;

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
	private Runnable fail;
	
	private ConncetionResult connected = null;
	
	void changePort(String port) {
		System.out.println("-changePort(" + port + ")-");
		params.PORT_NAME = port;
	}
	
	public void start() {
		connected = null;

		System.out.println("Rozpoczêto próbê po³¹czenia (port: " + params.PORT_NAME + ")");
		
		createCheckingThread().start();
	}

	private Thread createCheckingThread() {
		return new Thread(() -> {
			
			System.out.println("Uruchamiam w¹tek");
			connect(CONNECTING_TIMEOUT);
			System.out.println("connectedFlag: " + connected);
			
			if(connected == CONNECTED) {
				sleep(PROOF_MESSAGE_WAITING);
				checkDevice();
			} else {
				System.out.println("Port niedostêpny");
				Injection.run(fail);
			}
		});
	}

	private void checkDevice() {
		System.out.println("Sprawdzam");
		
		if(device.isCorrectDevice()) {
			System.out.println("\tCorrect device");
			Injection.run(success);
		} else {
			System.out.println("\tIncorrect device. Roz³¹czam.");
			device.disconnect();
			System.out.println("Roz³¹czy³em");
			Injection.run(fail);
		}
	}
	
	private void connect(int timeout) {
		Thread thread = new Thread(() -> connected = connect());
		
		connected = DURING;
		thread.setDaemon(true);
		thread.start();
		
		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (connected == DURING) connected = NO_RESPONSE;
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
