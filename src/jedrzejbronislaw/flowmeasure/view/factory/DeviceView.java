package jedrzejbronislaw.flowmeasure.view.factory;

import javafx.scene.Node;
import lombok.Getter;

public class DeviceView {

	@Getter public final Node livePane;
	@Getter public final Node table;
	@Getter public final Node chart;
	@Getter public final Node settingsPane;
	@Getter public final Node calibrationPane;
	
	
	public DeviceView(ViewFactory factory) {
		livePane        = factory.livePane();
		table           = factory.table();
		chart           = factory.chart();
		settingsPane    = factory.settingsPane();
		calibrationPane = factory.calibrationPane();
	}
}
