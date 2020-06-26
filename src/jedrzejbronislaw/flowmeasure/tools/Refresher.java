package jedrzejbronislaw.flowmeasure.tools;

import javafx.application.Platform;

public class Refresher {
	
	
	private class LoopThread {
		private final Thread t;
		private boolean end = false;
		private final int interval;
		
		public LoopThread(int interval, Runnable action) {
			this.interval = interval;
			
			t = new Thread(() -> {
				while(!end) {
					Platform.runLater(action);
					sleep();
					}
				});

			t.setDaemon(true);
			t.start();
		}

		public void end() {
			end = true;
		}

		public boolean isAlive() {
			return t.isAlive();
		}

		private void sleep() {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private final int interval;
	private final Runnable refresh;
	private LoopThread currentLoop = null;
	
	
	public Refresher(int interval, Runnable refresh) {
		this.interval = interval;
		this.refresh = refresh;
	}

	public void on() {
		currentLoop = new LoopThread(interval, refresh);
	}

	public void off() {
		if(currentLoop != null)
			currentLoop.end();
	}
	
	public boolean isWorking() {
		return  currentLoop != null &&
				currentLoop.isAlive();
	}
}
