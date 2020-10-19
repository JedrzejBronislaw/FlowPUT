package jedrzejbronislaw.flowmeasure.view.calibration;

import java.util.ArrayList;
import java.util.List;

import jedrzejbronislaw.flowmeasure.components.calibration.Calibration;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalibrationPaneBuilder extends Builder<CalibrationPaneController> {

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
			if(event(EventType.CALIBRATION_STARTS)) switchFlow(FlowConsumerType.CALIBRATION);
		});
		
		controller.setStop(() -> {
			if(event(EventType.CALIBRATION_ENDS))   switchFlow(FlowConsumerType.NONE);
		});
		
		controller.setSave(() -> {
			settings.setProperty(new RatioProperty(flowmeterNumber-1), calibration.getAveValue());
			settings.saveToFile();
		});
		
		controller.setOnChangeFlowmeter(this::setFlowmeter);
		controller.setNewMeasure(calibration::newMeasure);
		controller.setReset(calibration::reset);
		
		calibration.setAveValueListener(value -> controller.setCurrentAveValue(value));
		calibration.setValuesListener  (value -> controller.setCurrentValues(value));
		
		settings.addChangeListener(() -> {
			List<String> names = new ArrayList<>(Consts.FLOWMETERS_NUMBER);
			for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
				names.add(settings.getString(new FlowmeterNameProperty(i)));
			
			controller.setFlowmeterNames(names);
		});
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
