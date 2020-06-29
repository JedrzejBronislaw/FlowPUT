package jedrzejbronislaw.flowmeasure.states;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateTest {

	@Test
	public void testIsOneOf_ConnectionState() {
		ConnectionState comparator = ConnectionState.Connected;
		
		assertTrue(comparator.isOneOf(
						ConnectionState.Connected,
						ConnectionState.Connecting));
	}
	
	@Test
	public void testIsOneOf_none() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertFalse(comparator.isOneOf());
	}
	
	@Test
	public void testIsOneOf_one() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertTrue(comparator.isOneOf(ApplicationState.Idle));
	}

	@Test
	public void testIsOneOf_two() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertTrue(comparator.isOneOf(
						ApplicationState.Idle,
						ApplicationState.Calibration));
	}

	@Test
	public void testIsOneOf_twoFail() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertFalse(comparator.isOneOf(
						ApplicationState.Process,
						ApplicationState.Calibration));
	}

	@Test
	public void testIsOneOf_all() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertTrue(comparator.isOneOf(
						ApplicationState.Idle,
						ApplicationState.Calibration,
						ApplicationState.Process));
	}

	//----------
	
	
	@Test
	public void testIsOneOf_differentStateTypes() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertFalse(comparator.isOneOf(
						ConnectionState.Disconnected,
						ConnectionState.Connecting,
						ConnectionState.Connected));
	}
	
	@Test
	public void testIsOneOf_allStateTypesOnTheList_false() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertFalse(comparator.isOneOf(
						ConnectionState.Disconnected,
						ApplicationState.Calibration,
						ProcessState.Before));
	}
	
	@Test
	public void testIsOneOf_allStateTypesOnTheList_true() {
		ApplicationState comparator = ApplicationState.Idle;
		
		assertTrue(comparator.isOneOf(
						ConnectionState.Disconnected,
						ApplicationState.Calibration,
						ApplicationState.Idle,
						ProcessState.Before));
	}

}
