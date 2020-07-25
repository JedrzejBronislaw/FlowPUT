package jedrzejbronislaw.flowmeasure.flowDevice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;

public class FlowDeviceTest_introducing {

	private static final String INTRODUCING = "FD present!";
	
	private int[] flows;
	private MockUART mockUART;
	
	@Before
	public void prepare() {
		FlowDevice flowDevice = new FlowDevice();
		
		mockUART = new MockUART();
		
		flowDevice.setUartGenerator(params -> mockUART);
		flowDevice.setNewFlowsReceive(flow -> flows = flow);
		flowDevice.connect(new UARTParams());
	}

	@Test
	public void before_introducing() {
		mockUART.createMessage("^3;1;2;3;$");
		
		assertNull(flows);
	}

	@Test
	public void after_introducing() {
		int[] expectedsFlows = new int[] {1, 2, 3};

		mockUART.createMessage(INTRODUCING);
		mockUART.createMessage("^3;1;2;3;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}

	@Test
	public void incomplete_introducing_end() {
		mockUART.createMessage(INTRODUCING.substring(0, INTRODUCING.length()-1));
		mockUART.createMessage("^3;1;2;3;$");

		assertNull(flows);
	}

	@Test
	public void incomplete_introducing_begin() {
		mockUART.createMessage(INTRODUCING.substring(1));
		mockUART.createMessage("^3;1;2;3;$");

		assertNull(flows);
	}

	@Test
	public void wrong_introducing_lowerCase() {
		mockUART.createMessage(INTRODUCING.toLowerCase());
		mockUART.createMessage("^3;1;2;3;$");

		assertNull(flows);
	}

	@Test
	public void wrong_introducing_intro() {
		mockUART.createMessage("intro");
		mockUART.createMessage("^3;1;2;3;$");

		assertNull(flows);
	}

	@Test
	public void empty_introducing() {
		mockUART.createMessage("");
		mockUART.createMessage("^3;1;2;3;$");

		assertNull(flows);
	}

	@Test
	public void double_introducing() {
		int[] expectedsFlows = new int[] {1, 2, 3};

		mockUART.createMessage(INTRODUCING);
		mockUART.createMessage(INTRODUCING);
		mockUART.createMessage("^3;1;2;3;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}

	@Test
	public void random_before_introducing() {
		mockUART.createMessage("xgfchjvbkn");

		assertNull(flows);
	}
}
