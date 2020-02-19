package jedrzejbronislaw.flowmeasure.states;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateComparatorTest {

	@Test
	public void testIsOneOf_ConnectionState() {
		StateComparator<ConnectionState> competitor = new StateComparator<>(ConnectionState.Connected);
		
		assertTrue(competitor.isOneOf(
						ConnectionState.Connected,
						ConnectionState.Connecting));
	}
	
	@Test
	public void testIsOneOf_none() {
		StateComparator<ApplicationState> competitor = new StateComparator<>(ApplicationState.Idle);
		
		assertFalse(competitor.isOneOf());
	}
	
	@Test
	public void testIsOneOf_one() {
		StateComparator<ApplicationState> competitor = new StateComparator<>(ApplicationState.Idle);
		
		assertTrue(competitor.isOneOf(ApplicationState.Idle));
	}
	
	@Test
	public void testIsOneOf_getState() {
		StateComparator<ApplicationState> competitor = new StateComparator<>(ApplicationState.Idle);
		
		assertEquals(competitor.getState(), ApplicationState.Idle);
	}

	@Test
	public void testIsOneOf_two() {
		StateComparator<ApplicationState> competitor = new StateComparator<>(ApplicationState.Idle);
		
		assertTrue(competitor.isOneOf(
						ApplicationState.Idle,
						ApplicationState.Calibration));
	}

	@Test
	public void testIsOneOf_twoFail() {
		StateComparator<ApplicationState> competitor = new StateComparator<>(ApplicationState.Idle);
		
		assertFalse(competitor.isOneOf(
						ApplicationState.Process,
						ApplicationState.Calibration));
	}

	@Test
	public void testIsOneOf_all() {
		StateComparator<ApplicationState> competitor = new StateComparator<>(ApplicationState.Idle);
		
		assertTrue(competitor.isOneOf(
						ApplicationState.Idle,
						ApplicationState.Calibration,
						ApplicationState.Process));
	}

}
