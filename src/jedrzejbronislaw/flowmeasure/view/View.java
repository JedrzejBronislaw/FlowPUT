package jedrzejbronislaw.flowmeasure.view;

import jedrzejbronislaw.flowmeasure.UARTParams;

public interface View{// extends EventListener{

//	void connecting();
//	void disconnected();
//	void connected();
	
	void showCurrentFlow(int nr, int flow);
	
	UARTParams getUARTParams();
//	void diodeBlink();
	void showDialog(String title, String content, int closeDelay);
}
