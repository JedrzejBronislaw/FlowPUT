package jedrzejbronislaw.flowmeasure.view;

import jedrzejbronislaw.flowmeasure.UARTParams;

public interface ViewMediator {
	
	void showCurrentFlow(int nr, int flow);
	
	UARTParams getUARTParams();
}
