package jedrzejbronislaw.flowmeasure.application;

import java.util.LinkedList;
import java.util.List;

public class ComponentsLoader {

	private List<Runnable> loadMethods = new LinkedList<>();
	private boolean beforeLoading = true;
	
	
	public void load() {
		beforeLoading = false;
		loadMethods.forEach(lm -> lm.run());
	}
	
	public void addLoadMethod(Runnable loadMethod) {
		add(loadMethod);
	}
	
	private void add(Runnable loadMethod) {
		if (beforeLoading)
			loadMethods.add(loadMethod); else
			loadMethod.run();
	}
}
