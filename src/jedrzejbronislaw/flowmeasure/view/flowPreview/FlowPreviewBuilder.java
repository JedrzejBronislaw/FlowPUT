package jedrzejbronislaw.flowmeasure.view.flowPreview;

import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlowPreviewBuilder extends Builder<FlowPreviewController> {

	@Getter private String fxmlFilePath = "FlowPreview.fxml";

	private final int number;
	private final FlowConverter flowconverter;
	
	@Override
	protected void afterBuild() {
		controller.setNumber(number);
		controller.setFlowconverter(flowconverter);
	}
}
