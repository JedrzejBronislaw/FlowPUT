package jedrzejbronislaw.flowmeasure.tools;

import javafx.application.Platform;

public class Refresher {
	
	private class ThreadWithEnd {
		Thread t;
		boolean end = false;
	}

	private final int interval;
	private final Runnable refresh;
	private ThreadWithEnd currentThreat = null;
	
	
	public Refresher(int interval, Runnable refresh) {
		this.interval = interval;
		this.refresh = refresh;
	}

	public void on() {
		ThreadWithEnd thread = new ThreadWithEnd();
		thread.end = false;
		
		thread.t = new Thread(() -> {
			while(!thread.end) {
					Platform.runLater(refresh);
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
				
		thread.t.setDaemon(true);
		thread.t.start();
		
		currentThreat = thread;
	}
	
	public void off() {
		if(currentThreat != null)
			currentThreat.end = true;
	}
	
	public boolean isPenging() {
		return  currentThreat != null &&
				currentThreat.t.isAlive();
	}
}
