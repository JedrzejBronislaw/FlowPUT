package jedrzejbronislaw.flowmeasure.view.live;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.view.Builder;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import jedrzejbronislaw.flowmeasure.view.live.flowPreview.FlowPreviewBuilder;
import jedrzejbronislaw.flowmeasure.view.live.flowPreview.FlowPreviewController;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LivePaneBuilder extends Builder<LivePaneController> {

	@Getter private String fxmlFilePath = "LivePane.fxml";

	@NonNull private final ViewMediator viewMediator;
	@NonNull private final FlowConverters flowConverters;
	
	private List<FlowPreviewController> flowsPreviews = new ArrayList<>(Consts.FLOWMETERS_NUMBER);
	
	
	@Override
	protected void afterBuild() {
		
		controller.setResetAction(this::resetVolume);
		
		for(int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			controller.addFlowPreview(flowPreview(i));
	}
	
	private Node flowPreview(int number) {
		FlowPreviewBuilder builder = new FlowPreviewBuilder(number, flowConverters.get(number));
		builder.build();
		
		flowsPreviews.add(builder.getController());
		viewMediator.setFlowPreviewer(number, builder.getController()::addPulses);
		
		return builder.getNode();
	}
	
	private void resetVolume() {
		flowsPreviews.forEach(flow -> flow.resetSecPulse());
	}
}
