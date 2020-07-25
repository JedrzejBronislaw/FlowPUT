package jedrzejbronislaw.flowmeasure.components.connectionMonitor;

import java.time.LocalDateTime;

import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.loop.LoopThread;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionMonitor1 implements ConnectionMonitor {

	         private final int timeout;
	         private final int interval;
	@NonNull private Runnable timeoutEvent;
	
	@Getter private boolean alert = false;
	
	private LocalDateTime lastTime;
	private LoopThread monitor;

	
	@Override
	public void start() {
		refreshTime();
		alert = false;
		monitor = createMonitorLoop();
	}

	@Override
	public void stop() {
		if (monitor != null)
			monitor.end();
	}
		
	@Override
	public void newMessage() {
		refreshTime();
	}
	
	private void refreshTime() {
		lastTime = LocalDateTime.now();
	}
	
	private LoopThread createMonitorLoop() {
		return new LoopThread(interval, () -> {
			if(isTimeout()) {
				alert = true;
				monitor.end();
				Injection.run(timeoutEvent);
			}
		});
	}

	private boolean isTimeout() {
		return lastTime.plusSeconds(timeout).isBefore(LocalDateTime.now());
	}
	
	@Override
	public void event(EventType event) {
		if (event == EventType.ReceivedData) newMessage();
	}
}
