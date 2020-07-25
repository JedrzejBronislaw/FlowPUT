package jedrzejbronislaw.flowmeasure.tools.uart.connection;

import java.util.List;
import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class AutoConnection {

	private static final int WAITING_FOR_RELAUNCH = 500;
	
	@NonNull private UARTDevice device;
	@NonNull private List<String> portList;
	         private final int rate;
	
	@Setter private Consumer<String> ifSuccess;
	@Setter private Runnable ifFail;
	
	private int i = 0;
	private ConnectionAttempt attempt;
	
	private String getNextPort() {
		return (i < portList.size()) ? portList.get(portList.size()-1-(i++)) : null;
	}
	
	public void start() {
		UARTParams params = new UARTParams();
		
		params.DATA_RATE = rate;
		params.PORT_NAME = getNextPort();
		
		attempt = new ConnectionAttempt(device, params);
		
		attempt.setSuccess(this::singleSuccess);
		attempt.setFail(this::singleFail);

		attempt.start();
	}

	private void singleFail(ConncetionResult reason) {
		System.out.println(attempt.getParams().PORT_NAME + ": " + reason);

		if (reason == ConncetionResult.BUSY)
			relaunch(WAITING_FOR_RELAUNCH); else
			tryNextPort();
	}

	private void singleSuccess() {
		System.out.println(attempt.getParams().PORT_NAME + ": connected");

		Injection.run(ifSuccess, attempt.getParams().PORT_NAME);
	}

	private void tryNextPort() {
		String port = getNextPort();
		
		if(port == null)
			Injection.run(ifFail); else
			relaunch(port);
	}

	private void relaunch(String port) {
		attempt.changePort(port);
		attempt.start();
	}
	
	private void relaunch(int sleep) {
		sleep(sleep);
		attempt.start();
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
