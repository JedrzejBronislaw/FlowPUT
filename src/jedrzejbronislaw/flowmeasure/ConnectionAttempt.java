package jedrzejbronislaw.flowmeasure;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ConnectionAttempt {

	@NonNull
	private FlowDevice device;
	@NonNull
	@Getter
	private UARTParams params;
	
	@Setter
	private Runnable success;
	@Setter
	private Runnable fail;
	
	private boolean portNotAvailableFlag = false;
	
	void changePort(String port) {
		System.out.println("-changePort(" + port + ")-");
		params.PORT_NAME = port;
	}
	
	public void start() {
		Thread t1, t2;
		
		portNotAvailableFlag = false;

		System.out.println("Rozpoczêto próbê po³¹czenia (port: " + params.PORT_NAME + ")");
		
		t2 = new Thread(() -> {
			
			if(device.connect(params)) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				device.sendProofRequest();
			} else {
				portNotAvailableFlag = true;
			}
		});
		
		t1 = new Thread(() -> {

			
			System.out.println("Uruchamiam w¹tek");
			t2.start();
			System.out.println("W¹tek uruchomiony");
			System.out.println("Za 4 sek sprawdzê...");
			
			try {
				for(int i=0; i<40; i++)
					if(!portNotAvailableFlag)
						Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("portNotAvailableFlag: " + portNotAvailableFlag);
			
			if(portNotAvailableFlag) {
				System.out.println("Port niedostêpny");
				Injection.run(fail);
			} else {
			
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
		});
		
		t1.start();
	}
}
