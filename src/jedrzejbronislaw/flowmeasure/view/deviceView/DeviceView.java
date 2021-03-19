package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import lombok.Getter;

public class DeviceView {

	@Getter public final Node livePane;
	@Getter public final Node tablePane;
	@Getter public final Node chartPane;
	@Getter public final Node settingsPane;
	@Getter public final Node calibrationPane;
	
	
	public DeviceView(ViewFactory factory) {
		livePane        = factory.createLivePane();
		tablePane       = factory.createTablePane();
		chartPane       = factory.createChartPane();
		settingsPane    = factory.createSettingsPane();
		calibrationPane = factory.createCalibrationPane();
	}
}
