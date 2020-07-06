package jedrzejbronislaw.flowmeasure.view.view1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.UARTParams;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import lombok.Setter;

public class ViewMediator1 implements ViewMediator {

	private Map<Integer, Consumer<Integer>> flowViews = new HashMap<>();

	@Setter
	private Supplier<UARTParams> uartParamsGetter;
	
	public void setFlowPreviewer(int nr, Consumer<Integer> flowConsumer) {
		flowViews.put(nr, flowConsumer);
	}
	
	@Override
	public void showCurrentFlow(int flow, int nr) {
		flowViews.get(nr).accept(flow);
	}
	
	@Override
	public UARTParams getUARTParams() {
		return Injection.get(uartParamsGetter, null);
	}
}
