package jedrzejbronislaw.flowmeasure.flowDevice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class FlowDeviceTest_correctFormatLine {

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
	public void _6_123456() {
		int[] expectedsFlows = new int[] {1, 2, 3, 4, 5, 6};
		
		mockUART.createMessage("^6;1;2;3;4;5;6;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}
	
	@Test
	public void _6_123456_withoutEndingSemicolon() {
		int[] expectedsFlows = new int[] {1, 2, 3, 4, 5, 6};
		
		mockUART.createMessage("^6;1;2;3;4;5;6$");
		
		assertArrayEquals(expectedsFlows, flows);
	}
	
	@Test
	public void _6_big() {
		int[] expectedsFlows = new int[] {812, 522, 638, 246, 563, 960};
		
		mockUART.createMessage("^6;812;522;638;246;563;960;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}
	
	@Test
	public void one_very_big() {
		int[] expectedsFlows = new int[] {812, 522, 1234567890, 246, 563, 960};
		
		mockUART.createMessage("^6;812;522;1234567890;246;563;960;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}
	
	@Test
	public void _3_123456() {
		int[] expectedsFlows = new int[] {1, 2, 3};
		
		mockUART.createMessage("^3;1;2;3;4;5;6;$");

		assertArrayEquals(expectedsFlows, flows);
	}
	
	@Test
	public void _3_123() {
		int[] expectedsFlows = new int[] {1, 2, 3};
		
		mockUART.createMessage("^3;1;2;3;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}
	
	@Test
	public void _4_123() {
		mockUART.createMessage("^4;1;2;3;$");
		
		assertNull(flows);
	}
	
	@Test
	public void _5_123() {
		mockUART.createMessage("^5;1;2;3;$");
	
		assertNull(flows);
	}
	
	@Test
	public void negative_number() {
		int[] expectedsFlows = new int[] {1, -5, 3};
		
		mockUART.createMessage("^3;1;-5;3;$");
		
		assertArrayEquals(expectedsFlows, flows);
	}
	
	@Test
	public void one_letter() {
		mockUART.createMessage("^3;1;a;3;$");
		
		assertNull(flows);
	}
	
	@Test
	public void one_empty() {
		mockUART.createMessage("^3;1;;3;$");
		
		assertNull(flows);
	}
}
