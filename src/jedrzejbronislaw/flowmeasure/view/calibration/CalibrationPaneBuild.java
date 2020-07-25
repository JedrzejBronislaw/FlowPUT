package jedrzejbronislaw.flowmeasure.view.calibration;

import jedrzejbronislaw.flowmeasure.FlowManager;
import jedrzejbronislaw.flowmeasure.FlowManager.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalibrationPaneBuild extends Builder<CalibrationPaneController> {

	@Getter private String fxmlFilePath = "CalibrationPane.fxml";

	private final EventManager eventManager;
	private final FlowManager flowManager;
	private final Settings settings;
	private final Calibration calibration;
	private int flowmeterNumber;
	
	@Override
	protected void afterBuild() {
		controller.setStart(flowmeter -> {
			setFlowmeter(flowmeter);
			if(event(EventType.Calibration_Starts)) switchFlow(FlowConsumerType.Calibration);
		});
		
		controller.setStop(() -> {
			if(event(EventType.Calibration_Ends))   switchFlow(FlowConsumerType.None);
		});
		
		controller.setSet(() -> {
			settings.setProperty(new RatioProperty(flowmeterNumber-1), calibration.getAveValue());
			settings.saveToFile();
		});
		
		controller.setOnChangeFlowmeter(this::setFlowmeter);
		controller.setNewMeasure(calibration::newMeasure);
		controller.setReset(calibration::reset);
		
		calibration.setAveValueListener(value -> controller.setCurrentAveValue(value));
		calibration.setValuesListener  (value -> controller.setCurrentValues(value));
		eventManager.addListener(controller);
	}

	private boolean event(EventType event) {
		return eventManager.submitEvent(event);
	}

	private void switchFlow(FlowConsumerType flowConsumerType) {
		flowManager.setFlowConsumerType(flowConsumerType);
	}

	private void setFlowmeter(int flowmeter) {
		flowmeterNumber = flowmeter;
		calibration.setFlowmeter(flowmeter);
		calibration.reset();
	}
}
