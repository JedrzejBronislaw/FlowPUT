package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.ed.chart.ChartEDPane;
import jedrzejbronislaw.flowmeasure.view.ed.live.LiveEDPane;
import jedrzejbronislaw.flowmeasure.view.flow.table.MeasurementTable;

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
		return new MeasurementTable(this::getCurrentProcessRepo);
	}
	
	@Override
	public Node createSettingsPane(){
		return new Pane();
	}

	@Override
	public Node createCalibrationPane() {
		return new Pane();
	}

	@Override
	public Node createLivePane() {
		LiveEDPane livePane = new LiveEDPane();
		livePane.setViewMediator(viewMediator());
		
		return livePane;
	}
}
