package jedrzejbronislaw.flowmeasure.view;

import jedrzejbronislaw.flowmeasure.UARTParams;

public interface ViewMediator {
	
	void showCurrentFlow(int flow, int nr);
	
	UARTParams getUARTParams();
}
