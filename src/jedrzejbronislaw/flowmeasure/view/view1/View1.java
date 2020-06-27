package jedrzejbronislaw.flowmeasure.view.view1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import jedrzejbronislaw.flowmeasure.UARTParams;
import jedrzejbronislaw.flowmeasure.controllers.FlowPreviewController;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader.NodeAndController;
import jedrzejbronislaw.flowmeasure.view.View;

public class View1 implements View {

	interface showDialogInteface{
		void run(String title, String content, int closeDelay);
	}
	
	Map<Integer, NodeAndController<FlowPreviewController>> flowViews = new HashMap<>();


	showDialogInteface showDialog;
	
	Supplier<UARTParams> getUARTParams;
	
	@Override
	public void showCurrentFlow(int nr, int flow) {
		flowViews.get(nr).getController().addPulses(flow);
	}

	
	@Override
	public UARTParams getUARTParams() {
		return Injection.get(getUARTParams, null);
	}

	@Override
	public void showDialog(String title, String content, int closeDelay) {
		if(showDialog != null)
			showDialog.run(title, content, closeDelay);
	}

}
