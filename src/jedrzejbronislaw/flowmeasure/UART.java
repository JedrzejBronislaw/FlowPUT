package jedrzejbronislaw.flowmeasure;

import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import lombok.Setter;

public class UART {

	public static final String rates[] = {"300", "1200", "2400", "4800", "9600", "19200"};

	private SerialPort port;
	
//	SerialPort serialPort;
	private final String PORT_NAME;// = "COM6";

	private BufferedReader input;

	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private final int DATA_RATE;// = 9600;
	
	@Setter
	private Consumer<String> receiveMessage;
	
	public UART(UARTParams params) {
//		PORT_NAME = "COM6";//params.PORT_NAME;
//		DATA_RATE = 9600;//params.DATA_RATE;
		
		PORT_NAME = params.PORT_NAME;
		DATA_RATE = params.DATA_RATE;
		
		
		
		
//		connect();
	}
	
	public boolean isPortOpen() {
		if (port != null)
			return port.isOpen();
		return false;
	}
	
	public boolean connect() {
//		SerialPort ports[] = SerialPort.getCommPorts();

		port = SerialPort.getCommPort(PORT_NAME);
		// = ports[2];

		port.setBaudRate(DATA_RATE);
		port.openPort();

		if (port.isOpen()) {
			System.out.println("Port initialized!");
			// timeout not needed for event based reading
			// userPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
		} else {
			System.out.println("Port not available");
			return false;
		}
		
		port.addDataListener(new SerialPortDataListener() {
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}

			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
					return;
				byte[] newData = new byte[port.bytesAvailable()];
				int numRead = port.readBytes(newData, newData.length);
//				System.out.println("Read " + numRead + " bytes.");
//				System.out.println("|" + new String(newData) + "|");
				if(receiveMessage != null)
					receiveMessage.accept(new String(newData));
			}
		});
		
		return true;
	}
	
	public boolean send(String message) {
		if(!port.isOpen())
			return false;
		
		
		port.writeBytes(message.getBytes(), message.length());
		
		return true;
	}



	public static List<String> getRateList() {
		List<String> list = new LinkedList<String>();
		
		for(int i=0; i<rates.length; i++)
			list.add(rates[i]);
		
		return list;
	}

	public static List<String> getPortList() {

		List<String> list = new LinkedList<String>();
		
		SerialPort ports[] = SerialPort.getCommPorts();
		
		for(int i=0; i<ports.length; i++)
			list.add(ports[i].getSystemPortName());
		
		return list;
		
	}

	public void disconnect() {
		port.closePort();
	}

}
