package jedrzejbronislaw.flowmeasure.view.factory;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.calibration.CalibrationPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.chartPH.ChartPHPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.connection.UARTParamsBuilder;
import jedrzejbronislaw.flowmeasure.view.livePH.LivePHPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.settings.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.sidePane.SidePaneBuilder;
import jedrzejbronislaw.flowmeasure.view.table.MeasurementTableBuilder;

public class PHViewFactory extends ViewFactory {
	
	public PHViewFactory(Components components, ActionContainer actions) {
		super(components, actions);
	}


	@Override
	public Node uart() {
		UARTParamsBuilder builder = new UARTParamsBuilder(actions);
		builder.build();
		
		viewMediator().setUartParamsGetter(builder.getController()::getParams);
		addConnListener(builder.getController());
		
		return builder.getNode();
	}
	
	@Override
	public Node chart() {
		ChartPHPaneBuilder builder = new ChartPHPaneBuilder(this::getCurrentProcessRepo, flowconverters(), settings());
		builder.build();
		
		return builder.getNode();
	}

	@Override
	public Node table() {
		MeasurementTableBuilder builder = new MeasurementTableBuilder(this::getCurrentProcessRepo);
		builder.build();
		
		return builder.getNode();
	}
	
	@Override
	public Node sidePane(){
		SidePaneBuilder builder = new SidePaneBuilder(actions);
		builder.build();
		
		addAllStatesListener(builder.getController());
		addEventListener(builder.getController());
		
		return builder.getNode();
	}
	
	@Override
	public Node settingsPane(){
		SettingsPaneBuilder builder = new SettingsPaneBuilder(settings());
		builder.build();
		
		addAppListener(builder.getController());
		
		return builder.getNode();
	}

	@Override
	public Node calibrationPane() {
		CalibrationPaneBuilder builder = new CalibrationPaneBuilder(eventManager(), flowManager(), settings(), calibration());
		builder.build();
		
		addAllStatesListener(builder.getController());
		
		return builder.getNode();
	}

	@Override
	public Node livePane() {
		LivePHPaneBuilder builder = new LivePHPaneBuilder(viewMediator(), flowconverters(), settings());
		builder.build();
		
		return builder.getNode();
	}
}
