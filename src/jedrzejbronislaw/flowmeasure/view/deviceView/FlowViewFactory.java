package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.flow.calibration.CalibrationPane;
import jedrzejbronislaw.flowmeasure.view.flow.chart.ChartPane;
import jedrzejbronislaw.flowmeasure.view.flow.live.LivePane;
import jedrzejbronislaw.flowmeasure.view.flow.settings.SettingsPane;
import jedrzejbronislaw.flowmeasure.view.flow.table.MeasurementTable;

public class FlowViewFactory extends ViewFactory {

	public FlowViewFactory(Components components, ActionContainer actions) {
		super(components, actions);
	}

	
	@Override
	public Node createChartPane() {
		return new ChartPane(flowconverters(), settings(), this::getCurrentProcessRepo);
	}
	
	@Override
	public Node createTablePane() {
		return new MeasurementTable(this::getCurrentProcessRepo);
	}
	
	@Override
	public Node createSettingsPane(){
		SettingsPane settingsPane = new SettingsPane(settings());
		addAppListener(settingsPane);
		
		return settingsPane;
	}

	@Override
	public Node createCalibrationPane() {
		CalibrationPane calibrationPane = new CalibrationPane(eventManager(), flowManager(), settings(), calibration());
		addAllStatesListener(calibrationPane);
		
		return calibrationPane;
	}

	@Override
	public Node createLivePane() {
		return new LivePane(viewMediator(), flowconverters(), settings());
	}
}
