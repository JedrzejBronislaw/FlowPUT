package jedrzejbronislaw.flowmeasure.view;

import jedrzejbronislaw.flowmeasure.UARTParams;

public interface View{
	
	void showCurrentFlow(int nr, int flow);
	
	UARTParams getUARTParams();
}
