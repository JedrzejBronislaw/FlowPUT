package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.flow.calibration.CalibrationPane;
import jedrzejbronislaw.flowmeasure.view.flow.chart.ChartPane;
import jedrzejbronislaw.flowmeasure.view.flow.live.LivePane;
import jedrzejbronislaw.flowmeasure.view.flow.settings.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.flow.table.MeasurementTableBuilder;

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
		MeasurementTableBuilder builder = new MeasurementTableBuilder(this::getCurrentProcessRepo);
		builder.build();
		
		return builder.getNode();
	}
	
	@Override
	public Node createSettingsPane(){
		SettingsPaneBuilder builder = new SettingsPaneBuilder(settings());
		builder.build();
		
		addAppListener(builder.getController());
		
		return builder.getNode();
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
