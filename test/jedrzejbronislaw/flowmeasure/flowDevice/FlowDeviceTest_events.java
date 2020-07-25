package jedrzejbronislaw.flowmeasure.flowDevice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import lombok.AllArgsConstructor;

public class FlowDeviceTest_events {
	
	@AllArgsConstructor
	private class IndexedFlow {int index, flow;}
	
	private static final String RANDOM_MESSAGE = "rtzxtcfjygvkhb";
	
	private MockUART mockUART;
	
	private boolean newFlowsReceive = false;
	private boolean introducingEvent = false;
	private boolean incorrMessEvent = false;
	private boolean newSingleFlowEvent = false;
	
	private int[] flows;
	private String incorrectMessage;
	private List<IndexedFlow> singleFlowList = new LinkedList<>();
	
	
	@Before
	public void prepare() {
		FlowDevice flowDevice = new FlowDevice();
		
		mockUART = new MockUART();
		
		flowDevice.setUartGenerator(params -> mockUART);
		
		flowDevice.setDeviceConfirmation(() -> introducingEvent = true);
		flowDevice.setNewFlowsReceive(flow -> {
			newFlowsReceive = true;
			flows = flow;
		});
		flowDevice.setNewSingleFlowReceive((flow, index) -> {
			newSingleFlowEvent = true;
			singleFlowList.add(new IndexedFlow(index, flow));
		});
		flowDevice.setIncorrectMessageReceive(message -> {
			incorrMessEvent = true;
			incorrectMessage = message;
		});
		
		flowDevice.connect(new UARTParams());
	}
	
	private void resetEvents() {
		introducingEvent
		= newFlowsReceive
		= newSingleFlowEvent
		= incorrMessEvent
		= false;
	}
	
	private void introduceAndResetEvents() {
		mockUART.createMessage("FD present!");
		resetEvents();
	}

	private void assertNoEvents() {
		assertFalse(introducingEvent);
		assertFalse(newFlowsReceive);
		assertFalse(incorrMessEvent);
		assertFalse(newSingleFlowEvent);
	}

	private int eventsNumber() {
		int number = 0;
		
		if(introducingEvent)   number++;
		if(newFlowsReceive)    number++;
		if(incorrMessEvent)    number++;
		if(newSingleFlowEvent) number++;
		
		return number;
	}

	@Test
	public void introducing() {
		mockUART.createMessage("FD present!");
		
		assertEquals(1, eventsNumber());
		assertTrue(introducingEvent);
	}
	
	@Test
	public void incorrectMessage_beforeIntroducing() {
		mockUART.createMessage(RANDOM_MESSAGE);
		
		assertNoEvents();
		assertNull(incorrectMessage);
	}
	
	@Test
	public void incorrectMessage_afterIntroducing() {
		introduceAndResetEvents();
		mockUART.createMessage(RANDOM_MESSAGE);

		assertEquals(1, eventsNumber());
		assertTrue(incorrMessEvent);
		assertEquals(RANDOM_MESSAGE, incorrectMessage);
	}
	
	@Test
	public void dataLine_beforeIntroducing() {
		mockUART.createMessage("^3;1;2;3;$");
		
		assertNoEvents();
		assertNull(flows);
	}
	
	@Test
	public void dataLine_afterIntroducing() {
		int[] expectedsFlows = new int[] {4, 5, 6};
		introduceAndResetEvents();

		mockUART.createMessage("^3;4;5;6;$");

		assertEquals(2, eventsNumber());
		assertTrue(newFlowsReceive);
		assertTrue(newSingleFlowEvent);
		
		assertArrayEquals(expectedsFlows, flows);
		assertEquals(3, singleFlowList.size());
		assertEquals(0, singleFlowList.get(0).index);
		assertEquals(4, singleFlowList.get(0).flow);
		assertEquals(1, singleFlowList.get(1).index);
		assertEquals(5, singleFlowList.get(1).flow);
		assertEquals(2, singleFlowList.get(2).index);
		assertEquals(6, singleFlowList.get(2).flow);
	}
}
