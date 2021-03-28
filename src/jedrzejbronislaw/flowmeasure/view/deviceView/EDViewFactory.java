package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import jedrzejbronislaw.flowmeasure.view.ed.chart.ChartEDPane;
import jedrzejbronislaw.flowmeasure.view.ed.live.LiveEDPane;
import jedrzejbronislaw.flowmeasure.view.flow.table.MeasurementTable;

public class EDViewFactory extends ViewFactory {
	
	@Override
	public Node createChartPane() {
		return new ChartEDPane();
	}

	@Override
	public Node createTablePane() {
		return new MeasurementTable();
	}
	
	@Override
	public Node createSettingsPane() {
		return new Pane();
	}

	@Override
	public Node createCalibrationPane() {
		return new Pane();
	}

	@Override
	public Node createLivePane() {
		return new LiveEDPane();
	}
}
