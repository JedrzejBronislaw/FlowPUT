package jedrzejbronislaw.flowmeasure.view;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import jedrzejbronislaw.flowmeasure.devices.DeviceType;
import jedrzejbronislaw.flowmeasure.view.deviceView.DeviceView;
import jedrzejbronislaw.flowmeasure.view.mainWindow.MainWindow;
import lombok.Setter;

public class ViewManager {

	@Setter private MainWindow mainWindow;
	private final Map<DeviceType, DeviceView> deviceViews = new HashMap<>();

	
	public void addDeviceView(DeviceType flowdevice, DeviceView deviceView) {
		deviceViews.put(flowdevice, deviceView);
	}

	public void setDeviceView(DeviceType device) {
		DeviceView view = deviceViews.get(device);
		if (view == null) return;
		
		Platform.runLater(() -> {
			mainWindow.setLivePane       (view.getLivePane());
			mainWindow.setTablePane      (view.getTablePane());
			mainWindow.setChartPane      (view.getChartPane());
			mainWindow.setSettingsPane   (view.getSettingsPane());
			mainWindow.setCalibrationPane(view.getCalibrationPane());
		});
	}
}
