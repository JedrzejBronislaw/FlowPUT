package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.controllers.SidePaneController;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SidePaneBuilder extends Builder<SidePaneController> {

	@Getter private String fxmlFilePath = "SidePane.fxml";

	private final ActionContainer actions;
	
	@Override
	void afterBuild() {
		controller.setStartButtonAction(actions::startProcess);
		controller.setEndButtonAction(  actions::endProcess);
		controller.setSaveButtonAction( actions::saveProcess);
		controller.setCloseButtonAction(actions::closeProcess);
	}
}
