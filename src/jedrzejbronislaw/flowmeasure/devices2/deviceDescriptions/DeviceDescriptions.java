package jedrzejbronislaw.flowmeasure.devices2.deviceDescriptions;

import jedrzejbronislaw.flowmeasure.devices2.DeviceType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DeviceDescriptions {

	FlowDevice(DeviceType.FlowDevice, new FlowmeterDesc()),
	EDDevice  (DeviceType.EDDevice,   new EDDeviceDesc());
	
	
	private final DeviceType deviceType;
	private final DeviceDescription description;
	
	
	public static DeviceDescription get(DeviceType deviceType) {
		for (DeviceDescriptions descType : values())
			if (descType.deviceType == deviceType)
				return descType.description;
		
		return null;
	}
}
