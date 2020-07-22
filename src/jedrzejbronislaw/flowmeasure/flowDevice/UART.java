package jedrzejbronislaw.flowmeasure.flowDevice;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class UART implements IUART {

	public static final String rates[] = {"300", "1200", "2400", "4800", "9600", "19200"};

	private SerialPort port;
	
	private final String PORT_NAME;
	private final int DATA_RATE;

	@Setter
	private Consumer<String> receiveMessage;
	
	
	public UART(UARTParams params) {		
		PORT_NAME = params.PORT_NAME;
		DATA_RATE = params.DATA_RATE;
	}
	
	public boolean isPortOpen() {
		return (port != null && port.isOpen());
	}
	
	public boolean connect() {
		port = SerialPort.getCommPort(PORT_NAME);
		port.setBaudRate(DATA_RATE);
		port.openPort();
		
		if (!port.isOpen()) {
			System.out.println("Port not available");
			return false;
		}
		
		System.out.println("Port initialized!");
		
		port.addDataListener(generateDataListener());
		
		return true;
	}

	private SerialPortDataListener generateDataListener() {
		return new SerialPortDataListener() {
			
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}

			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
				
				byte[] newData = new byte[port.bytesAvailable()];
				port.readBytes(newData, newData.length);
				
				Injection.run(receiveMessage, new String(newData));
			}
		};
	}

	public void disconnect() {
		port.closePort();
	}
	
	public boolean send(String message) {
		if(!port.isOpen()) return false;
		
		port.writeBytes(message.getBytes(), message.length());
		
		return true;
	}



	public static List<String> getRateList() {
		return Stream.of(rates).collect(Collectors.toList());
	}

	public static List<String> getPortList() {
		return Stream.of(SerialPort.getCommPorts())
				.map(port -> port.getSystemPortName())
				.collect(Collectors.toList());
	}
}
