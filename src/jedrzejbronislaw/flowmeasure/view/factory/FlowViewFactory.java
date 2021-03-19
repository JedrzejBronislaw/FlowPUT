package jedrzejbronislaw.flowmeasure.view.factory;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.calibration.CalibrationPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.chart.ChartPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.live.LivePaneBuilder;
import jedrzejbronislaw.flowmeasure.view.settings.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.table.MeasurementTableBuilder;

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
