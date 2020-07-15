package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Session.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.controllers.CalibrationPaneController;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.settings.PropertyName;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalibrationPaneBuild extends Builder<CalibrationPaneController> {

	@Getter private String fxmlFilePath = "CalibrationPane.fxml";

	private final EventManager eventManager;
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
			settings.setProperty(PropertyName.PULSE_PER_LITRE, calibration.getValue());
			settings.write();
		});
		
		eventManager.addListener(controller);
	}
}
