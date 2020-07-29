package jedrzejbronislaw.flowmeasure.view.sidePane;

import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SidePaneBuilder extends Builder<SidePaneController> {

	@Getter private String fxmlFilePath = "SidePane.fxml";

	private final ActionContainer actions;
	
	@Override
	protected void afterBuild() {
		controller.setBeginButtonAction(actions::startProcess);
		controller.setEndButtonAction(  actions::endProcess);
		controller.setSaveButtonAction( actions::saveProcess);
		controller.setCloseButtonAction(actions::closeProcess);
	}
}
