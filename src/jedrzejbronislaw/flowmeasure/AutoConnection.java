package jedrzejbronislaw.flowmeasure;

import java.util.List;
import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.flowDevice.FlowDevice;
import jedrzejbronislaw.flowmeasure.flowDevice.UARTParams;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class AutoConnection {

	@NonNull private FlowDevice device;
	@NonNull private List<String> portList;
	         private final int rate;
	
	@Setter private Consumer<String> ifSuccess;
	@Setter private Runnable ifFail;
	
	private int i = 0;
	private ConnectionAttempt attempt;
	
	private String getNextPort() {
		return (i < portList.size()) ? portList.get(i++) : null;
	}
	
	public void start() {
		UARTParams params = new UARTParams();
		
		params.DATA_RATE = rate;
		params.PORT_NAME = getNextPort();
		
		attempt = new ConnectionAttempt(device, params);
		
		attempt.setSuccess(this::singleSuccess);
		attempt.setFail(this::singleFail);

		System.out.println("__najpierw sprobojemy z " + params.PORT_NAME);
		attempt.start();
	}

	private void singleFail() {
		String port = getNextPort();
		System.out.println("__niepowodzenie czesciowe sprobujmy " + port);
		
		if(port == null)
			Injection.run(ifFail); else
			relaunch(port);
	}

	private void singleSuccess() {
		System.out.println("__sukces");
		Injection.run(ifSuccess, attempt.getParams().PORT_NAME);
	}
	
	private void relaunch(String port) {
		attempt.changePort(port);
		System.out.println("zmieniono port na " + port);
		
		attempt.start();
	}
}
