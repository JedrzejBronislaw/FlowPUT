package jedrzejbronislaw.flowmeasure.view.flow.live.flowPreview;

import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverter;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlowPreviewBuilder extends Builder<FlowPreviewController> {

	@Getter private String fxmlFilePath = "FlowPreview.fxml";

	private final FlowConverter flowconverter;
	
	@Override
	protected void afterBuild() {
		controller.setName("");
		controller.setFlowconverter(flowconverter);
	}
}
