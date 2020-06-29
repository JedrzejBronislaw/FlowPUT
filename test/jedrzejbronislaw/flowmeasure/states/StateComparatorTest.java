package jedrzejbronislaw.flowmeasure.states;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateComparatorTest {

	@Test
	public void testIsOneOf_ConnectionState() {
		StateComparator comparator = new StateComparator(ConnectionState.Connected);
		
		assertTrue(comparator.isOneOf(
						ConnectionState.Connected,
						ConnectionState.Connecting));
	}
	
	@Test
	public void testIsOneOf_none() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertFalse(comparator.isOneOf());
	}
	
	@Test
	public void testIsOneOf_one() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertTrue(comparator.isOneOf(ApplicationState.Idle));
	}
	
	@Test
	public void testIsOneOf_getState() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertEquals(comparator.getState(), ApplicationState.Idle);
	}

	@Test
	public void testIsOneOf_two() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertTrue(comparator.isOneOf(
						ApplicationState.Idle,
						ApplicationState.Calibration));
	}

	@Test
	public void testIsOneOf_twoFail() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertFalse(comparator.isOneOf(
						ApplicationState.Process,
						ApplicationState.Calibration));
	}

	@Test
	public void testIsOneOf_all() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertTrue(comparator.isOneOf(
						ApplicationState.Idle,
						ApplicationState.Calibration,
						ApplicationState.Process));
	}

	//----------
	
	
	@Test
	public void testIsOneOf_differentStateTypes() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertFalse(comparator.isOneOf(
						ConnectionState.Disconnected,
						ConnectionState.Connecting,
						ConnectionState.Connected));
	}
	
	@Test
	public void testIsOneOf_allStateTypesOnTheList_false() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertFalse(comparator.isOneOf(
						ConnectionState.Disconnected,
						ApplicationState.Calibration,
						ProcessState.Before));
	}
	
	@Test
	public void testIsOneOf_allStateTypesOnTheList_true() {
		StateComparator comparator = new StateComparator(ApplicationState.Idle);
		
		assertTrue(comparator.isOneOf(
						ConnectionState.Disconnected,
						ApplicationState.Calibration,
						ApplicationState.Idle,
						ProcessState.Before));
	}

}
