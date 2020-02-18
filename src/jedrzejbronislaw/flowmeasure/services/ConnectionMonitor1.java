package jedrzejbronislaw.flowmeasure.services;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

import javafx.application.Platform;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionMonitor1 implements ConnectionMonitor{

//	@NonNull
	final int timeout;

//	@NonNull
	final int interval;

	@NonNull
	private Runnable timeoutEvent; 
	
	@Getter
	private boolean alert = false;
	
	private LocalDateTime lastTime;
	
	private boolean monitorActive = false;
	private Thread monitor;


	
	@Override
	public void start() {
		refreshTime();
		alert = false;
		monitorActive = true;
		monitor = createMonitorThread();
		monitor.start();
	}


	@Override
	public void stop() {
		monitorActive = false;
	}
	
		
	@Override
	public void newMessage() {
		refreshTime();
	}
	
	private void refreshTime() {
		lastTime = LocalDateTime.now();
	}
	
	private Thread createMonitorThread() {
		return new Thread(() -> {
			while(monitorActive) {
				
//				System.out.println("Monitor...");
				
				if(lastTime.plusSeconds(timeout).isBefore(LocalDateTime.now())) {
					alert = true;
					monitorActive = false;
					if (timeoutEvent != null)
						Platform.runLater(timeoutEvent);
				}
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
