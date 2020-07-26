package jedrzejbronislaw.flowmeasure.tools;

import java.util.ArrayList;
import java.util.List;

public class SimpleListenerManager {
	
	private List<Runnable> listeners = new ArrayList<>();
	
	public void action() {
		listeners.forEach(listener -> listener.run());
	}
	
	public void add(Runnable listener) {
		listeners.add(listener);
		listener.run();
	}
}
