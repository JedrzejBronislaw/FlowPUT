package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.flow.calibration.CalibrationPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.flow.chart.ChartPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.flow.live.LivePaneBuilder;
import jedrzejbronislaw.flowmeasure.view.flow.settings.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.flow.table.MeasurementTableBuilder;

public class FlowViewFactory extends ViewFactory {

	public FlowViewFactory(Components components, ActionContainer actions) {
		super(components, actions);
	}

	
	@Override
	public Node createChartPane() {
		ChartPaneBuilder builder = new ChartPaneBuilder(this::getCurrentProcessRepo, flowconverters(), settings());
		builder.build();
		
		return builder.getNode();
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
		CalibrationPaneBuilder builder = new CalibrationPaneBuilder(eventManager(), flowManager(), settings(), calibration());
		builder.build();
		
		addAllStatesListener(builder.getController());
		
		return builder.getNode();
	}

	@Override
	public Node createLivePane() {
		LivePaneBuilder builder = new LivePaneBuilder(viewMediator(), flowconverters(), settings());
		builder.build();
		
		return builder.getNode();
	}
}
