package jedrzejbronislaw.flowmeasure.tools.loop;

public class Refresher {
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
		if (currentLoop != null)
			currentLoop.end();
	}
	
	public boolean isWorking() {
		return  currentLoop != null &&
				currentLoop.isAlive();
	}
}
