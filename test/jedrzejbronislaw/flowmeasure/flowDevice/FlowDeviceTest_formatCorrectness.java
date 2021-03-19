package jedrzejbronislaw.flowmeasure.flowDevice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.devices.flowDevice.FlowDevice;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;

public class FlowDeviceTest_formatCorrectness {
	
	private int[] flows;
	private MockUART mockUART;
	
	@Before
	public void prepare() {
		FlowDevice flowDevice = new FlowDevice();
		
		mockUART = new MockUART();
		
		flowDevice.setUartGenerator(params -> mockUART);
		flowDevice.setNewFlowsReceive(flow -> flows = flow);
		flowDevice.connect(new UARTParams());
		mockUART.createMessage("FD present!");
	}

	@Test
	public void correct_line() {
		int[] expectedsFlows = new int[] {1, 2, 3};

		mockUART.createMessage("^3;1;2;3;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}

	@Test
	public void without_beginning() {
		mockUART.createMessage("3;1;2;3;$");
		
		assertNull(flows);
	}

	@Test
	public void without_end() {
		mockUART.createMessage("^3;1;2;3;");
		
		assertNull(flows);
	}

	@Test
	public void wrong_beginning() {
		mockUART.createMessage("_3;1;2;3;$");
		
		assertNull(flows);
	}

	@Test
	public void wrong_end() {
		mockUART.createMessage("^3;1;2;3;_");
		
		assertNull(flows);
	}

	@Test
	public void multiline_oneWrongLine() {
		int[] expectedsFlows = new int[] {4, 5, 6};

		mockUART.createMessage("^3;1;2;3;$");
		mockUART.createMessage("zgfdxcnvbg");
		mockUART.createMessage("^3;4;5;6;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}

	@Test
	public void something_after_end() {
		mockUART.createMessage("^3;1;2;3;$hxtcfyjguhk");
		
		assertNull(flows);
	}
}
