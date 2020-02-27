package jedrzejbronislaw.flowmeasure.tools;

import javafx.application.Platform;

public class Refresher {
	private boolean end = false;
	
	
	public Refresher(int interval, Runnable refresh) {
		Thread t = new Thread(() -> {
			while(!end) {
				Platform.runLater(refresh);
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		t.setDaemon(true);
		t.start();
					
	}
	
	public void off() {
		end = true;
	}
}
