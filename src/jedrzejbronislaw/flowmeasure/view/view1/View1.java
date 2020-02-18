package jedrzejbronislaw.flowmeasure.view.view1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.UARTParams;
import jedrzejbronislaw.flowmeasure.controllers.FlowPreviewController;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader.NodeAndController;
import jedrzejbronislaw.flowmeasure.view.View;

public class View1 implements View {

	interface showDialogInteface{
		void run(String title, String content, int closeDelay);
	}
	
	Map<Integer, NodeAndController<FlowPreviewController>> flowViews = new HashMap<>();

//	Runnable connecting;
//	Runnable disconnected;
//	Runnable connected;
	
//	Runnable diodeBlink;
	showDialogInteface showDialog;
	
	Supplier<UARTParams> getUARTParams;
	
//	@Override
//	public void connecting() {
//		if(connecting != null)
//			connecting.run();
//	}
//
//	@Override
//	public void disconnected() {
//		if(disconnected != null)
//			disconnected.run();
//	}
//
//	@Override
//	public void connected() {
//		if(connected != null)
//			connected.run();
//	}
	
	@Override
	public void showCurrentFlow(int nr, int flow) {
		flowViews.get(nr).getController().addPulses(flow);
	}

	
	@Override
	public UARTParams getUARTParams() {
		if (getUARTParams != null)
			return getUARTParams.get();

		return null;
	}

//	@Override
//	public void diodeBlink() {
//		if(diodeBlink != null)
//			diodeBlink.run();
//	}

	@Override
	public void showDialog(String title, String content, int closeDelay) {
		if(showDialog != null)
			showDialog.run(title, content, closeDelay);
	}

//	@Override
//	public void event(EventType event) {
//		switch (event) {
//		case ConnectionSuccessful:
//			connected(); break;
//		case ConnectionFailed:
//			disconnected(); break;
//		case LostConnection:
//			disconnected(); break;
//
//		default:
//			break;
//		}
//	}
}
