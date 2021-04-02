package jedrzejbronislaw.flowmeasure.devices2;

import jedrzejbronislaw.flowmeasure.view.deviceView.DeviceView;
import jedrzejbronislaw.flowmeasure.view.deviceView.UniversalViewFactory;

public class DeviceViewFactory {

	public static DeviceView create(DeviceType deviceType) {
		return new DeviceView(new UniversalViewFactory(deviceType));
	}

}
