package jedrzejbronislaw.flowmeasure;

import java.util.List;
import java.util.function.Consumer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ConnectionsAttempts {

	@NonNull
	private FlowDevice device;
	@NonNull
	private List<String> portList;
//	private String portList[];
//	@NonNull
	final private int rate;
	
	@Setter
	private Consumer<String> success;
	@Setter
	private Runnable fail;
	
	private int i = 0;
	private String getNextPort() {
		if(i < portList.size())
			return portList.get(i++);
		else
			return null;
	}
	
	public void start() {
//		int i = 0;
		UARTParams params = new UARTParams();
		
		params.DATA_RATE = rate;
		params.PORT_NAME = getNextPort();//portList[i];
		
		ConnectionAttempt attempt = new ConnectionAttempt(device, params);
		
		

		Runnable success2;
		Runnable fail2;
		
		success2 = () -> {
			System.out.println("__sukces");
			if(success != null)
				success.accept(attempt.getParams().PORT_NAME);//portList[i]);
		};
		
		fail2 = () -> {
//			i++;
			String port = getNextPort();
			System.out.println("__niepowodzenie czesciowe sprobujmy " + port);
			
//			if(i >= portList.length)
			if(port == null) {
				if(fail != null)
					fail.run();
			} else {
//				params.PORT_NAME = portList[i];
//				attempt.changePort(portList[i]);// = new ConnectionAttempt(device, params);
				attempt.changePort(port);// = new ConnectionAttempt(device, params);
				/*
				UARTParams params2 = new UARTParams();
				params2.DATA_RATE = rate;
				params2.PORT_NAME = port;
				
				ConnectionAttempt ca = new ConnectionAttempt(device, params2);
				
				ca.setSuccess(success2);
				ca.setFail(fail2);
				*/
				System.out.println("zmieniono port na " + port);
				
				attempt.start();
			}

		};

		attempt.setSuccess(success2);
		attempt.setFail(fail2);
		
		System.out.println("__najpierw sprobojemy z " + params.PORT_NAME);
		attempt.start();
		
	}
}
