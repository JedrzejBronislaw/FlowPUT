package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Session.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.controllers.CalibrationPaneController;
import jedrzejbronislaw.flowmeasure.events.EventManager1;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalibrationPaneBuild extends Builder<CalibrationPaneController> {

	@Getter private String fxmlFilePath = "CalibrationPane.fxml";

	private final EventManager1 eventManager;
	private final Session session;
	private final Settings settings;
	private final Calibration calibration;
	
	@Override
	void afterBuild() {
		controller.setStart(() -> {
			if(eventManager.submitEvent(EventType.Calibration_Starts))
				session.setFlowConsumerType(FlowConsumerType.Calibration);
		});
		
		controller.setStop(() -> {
			if(eventManager.submitEvent(EventType.Calibration_Ends))
				session.setFlowConsumerType(FlowConsumerType.None);
		});
		
		controller.setReset(calibration::reset);
		
		calibration.setValueListener(value -> controller.setCurrentValue(value));
		
		controller.setSet(() -> {
			settings.setPulsePerLitre(calibration.getValue());
			settings.write();
		});
		
		eventManager.addListener(controller);
	}
}