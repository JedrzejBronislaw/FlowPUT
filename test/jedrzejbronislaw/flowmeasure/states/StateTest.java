package jedrzejbronislaw.flowmeasure.states;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateTest {

	@Test
	public void testIsOneOf_ConnectionState() {
		ConnectionState comparator = ConnectionState.CONNECTED;
		
		assertTrue(comparator.isOneOf(
						ConnectionState.CONNECTED,
						ConnectionState.CONNECTING));
	}
	
	@Test
	public void testIsOneOf_none() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertFalse(comparator.isOneOf());
	}
	
	@Test
	public void testIsOneOf_one() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertTrue(comparator.isOneOf(ApplicationState.IDLE));
	}

	@Test
	public void testIsOneOf_two() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertTrue(comparator.isOneOf(
						ApplicationState.IDLE,
						ApplicationState.CALIBRATION));
	}

	@Test
	public void testIsOneOf_twoFail() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertFalse(comparator.isOneOf(
						ApplicationState.PROCESS,
						ApplicationState.CALIBRATION));
	}

	@Test
	public void testIsOneOf_all() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertTrue(comparator.isOneOf(
						ApplicationState.IDLE,
						ApplicationState.CALIBRATION,
						ApplicationState.PROCESS));
	}

	//----------
	
	
	@Test
	public void testIsOneOf_differentStateTypes() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertFalse(comparator.isOneOf(
						ConnectionState.DISCONNECTED,
						ConnectionState.CONNECTING,
						ConnectionState.CONNECTED));
	}
	
	@Test
	public void testIsOneOf_allStateTypesOnTheList_false() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertFalse(comparator.isOneOf(
						ConnectionState.DISCONNECTED,
						ApplicationState.CALIBRATION,
						ProcessState.BEFORE));
	}
	
	@Test
	public void testIsOneOf_allStateTypesOnTheList_true() {
		ApplicationState comparator = ApplicationState.IDLE;
		
		assertTrue(comparator.isOneOf(
						ConnectionState.DISCONNECTED,
						ApplicationState.CALIBRATION,
						ApplicationState.IDLE,
						ProcessState.BEFORE));
	}

}
