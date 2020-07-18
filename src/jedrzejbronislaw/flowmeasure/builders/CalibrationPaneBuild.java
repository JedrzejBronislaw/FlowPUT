package jedrzejbronislaw.flowmeasure.builders;

import jedrzejbronislaw.flowmeasure.FlowManager;
import jedrzejbronislaw.flowmeasure.FlowManager.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.controllers.CalibrationPaneController;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalibrationPaneBuild extends Builder<CalibrationPaneController> {

	@Getter private String fxmlFilePath = "CalibrationPane.fxml";

	private final EventManager eventManager;
	private final FlowManager flowManager;
	private final Settings settings;
	private final Calibration calibration;
	
	@Override
	void afterBuild() {
		controller.setStart(() -> {
			if(event(EventType.Calibration_Starts)) switchFlow(FlowConsumerType.Calibration);
		});
		
		controller.setStop(() -> {
			if(event(EventType.Calibration_Ends))   switchFlow(FlowConsumerType.None);
		});
		
		controller.setSet(() -> {
			settings.setProperty(AppProperties.PULSE_PER_LITRE, calibration.getValue());
			settings.saveToFile();
		});
		
		controller.setReset(calibration::reset);
		
		calibration.setValueListener(value -> controller.setCurrentValue(value));
		eventManager.addListener(controller);
	}

	private boolean event(EventType event) {
		return eventManager.submitEvent(event);
	}

	private void switchFlow(FlowConsumerType flowConsumerType) {
		flowManager.setFlowConsumerType(flowConsumerType);
	}
}
