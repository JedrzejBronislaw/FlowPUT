package jedrzejbronislaw.flowmeasure.view.view1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.UARTParams;
import jedrzejbronislaw.flowmeasure.controllers.FlowPreviewController;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;

public class ViewMediator1 implements ViewMediator {

	Map<Integer, FlowPreviewController> flowViews = new HashMap<>();

	Supplier<UARTParams> getUARTParams;
	
	
	@Override
	public void showCurrentFlow(int nr, int flow) {
		flowViews.get(nr).addPulses(flow);
	}
	
	@Override
	public UARTParams getUARTParams() {
		return Injection.get(getUARTParams, null);
	}
}
