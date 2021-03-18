package jedrzejbronislaw.flowmeasure.tools.uart.connection;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.uart.UART;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MultiDeviceAutoConnection {

	@NonNull private final List<UARTDevice> devices;
	@NonNull private List<String> portList;
	         private final int rate;
	
	private int deviceI = 0;
	
	@Setter private BiConsumer<UARTDevice, String> ifSuccess;
	@Setter private Runnable ifFail;
	
	
	public void start() {
		newAutoConnection().ifPresent(connection -> connection.start());
	}
	
	private Optional<AutoConnection> newAutoConnection() {
		if (deviceI >= devices.size()) {
			deviceI = 0;
			Injection.run(ifFail);
			return Optional.empty();
		}
		
		AutoConnection autoConnection = createAutoConnection(devices.get(deviceI++));
		if (autoConnection == null) {
			Injection.run(ifFail);
			return Optional.empty();
		}
		
		return Optional.of(autoConnection);
	}
	
	private AutoConnection createAutoConnection(UARTDevice device) {
		if (device == null || portList == null || portList.isEmpty()) return null;
		
		AutoConnection autoConn = new AutoConnection(device, portList, rate);
		System.out.println(UART.getPortList());
		
		autoConn.setIfFail(() -> {
			System.out.println("Pod ¿adnym portem nie znaleziono urz¹dzenia " + device.getName());
			start();
		});
		
		autoConn.setIfSuccess(port -> {
			System.out.println("Uda³o po³¹czyæ siê z urz¹dzeniem " + device.getName() + " na porcie: " + port);
			Injection.run(ifSuccess, device, port);
		});
		
		return autoConn;
	}
}
