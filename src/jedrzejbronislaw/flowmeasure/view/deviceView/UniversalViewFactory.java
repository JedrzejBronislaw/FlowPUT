package jedrzejbronislaw.flowmeasure.view.deviceView;

import java.util.List;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.devices2.Device;
import jedrzejbronislaw.flowmeasure.devices2.DeviceType;
import jedrzejbronislaw.flowmeasure.devices2.SensorType;
import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.DeviceDescription;
import jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions.DeviceDescriptions;
import jedrzejbronislaw.flowmeasure.view.universal.live.UniLivePane;
import jedrzejbronislaw.flowmeasure.view.universal.live.preview.LivePreview;

public class UniversalViewFactory extends ViewFactory {

	private final DeviceType type;
	private Device device;
	
	
	public UniversalViewFactory(DeviceType type) {
		this.type = type;
		
		Components.getComponentsLoader().addLoadMethod(() ->
			device = Components.getDeviceManager().getDevice(type)
		);
	}


	@Override
	public Node createLivePane() {
		DeviceDescription desc = DeviceDescriptions.get(type);
		List<SensorType> sensors = desc.getSensors();
		UniLivePane livePane = new UniLivePane();
		
		for (int i=0; i<sensors.size(); i++) {
			SensorType sensorType = sensors.get(i);
			LivePreview preview = new LivePreview(sensorType.toString());
			device.setSensorLiveOutput(i, preview::setValue);

			livePane.addFlowPreview(preview);
		}
		
		return livePane;
	}

	@Override
	public Node createTablePane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node createChartPane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node createSettingsPane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node createCalibrationPane() {
		// TODO Auto-generated method stub
		return null;
	}
}
