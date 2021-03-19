package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.calibration.CalibrationPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.chartED.ChartEDPane;
import jedrzejbronislaw.flowmeasure.view.liveED.LiveEDPane;
import jedrzejbronislaw.flowmeasure.view.settings.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.table.MeasurementTableBuilder;

public class EDViewFactory extends ViewFactory {
	
	public EDViewFactory(Components components, ActionContainer actions) {
		super(components, actions);
	}


	@Override
	public Node createChartPane() {
		return new ChartEDPane(flowconverters(), settings(), this::getCurrentProcessRepo);
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
		LiveEDPane livePane = new LiveEDPane();
		livePane.setViewMediator(viewMediator());
		
		return livePane;
	}
}
