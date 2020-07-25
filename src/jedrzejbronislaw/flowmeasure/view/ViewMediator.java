package jedrzejbronislaw.flowmeasure.view;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import lombok.Setter;

public class ViewMediator {

	private Map<Integer, Consumer<Integer>> flowViews = new HashMap<>();

	@Setter
	private Supplier<UARTParams> uartParamsGetter;
	
	public void setFlowPreviewer(int nr, Consumer<Integer> flowConsumer) {
		flowViews.put(nr, flowConsumer);
	}
	
	public void showCurrentFlow(int flow, int nr) {
		Consumer<Integer> view = flowViews.get(nr);
		
		if (view != null) view.accept(flow);
	}
	
	public UARTParams getUARTParams() {
		return Injection.get(uartParamsGetter);
	}
}
