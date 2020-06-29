package jedrzejbronislaw.flowmeasure.states;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationStateTest {

	@Test
	public void test_isOneOf_true() {
		boolean out;
		ApplicationState state = ApplicationState.Process;
		
		out = state.isOneOf(
				ApplicationState.Calibration,
				ApplicationState.Process);
		
		assertTrue(out);
	}

	@Test
	public void test_isOneOf_false() {
		boolean out;
		ApplicationState state = ApplicationState.Process;
		
		out = state.isOneOf(
				ApplicationState.Calibration,
				ApplicationState.Idle);
		
		assertFalse(out);
	}

	@Test
	public void test_isOneOf_otherStateTypes_false() {
		boolean out;
		ApplicationState state = ApplicationState.Idle;
		
		out = state.isOneOf(
				ConnectionState.Disconnected,
				ApplicationState.Calibration,
				ProcessState.Before);
		
		assertFalse(out);
	}

	@Test
	public void test_isOneOf_otherStateTypes_true() {
		boolean out;
		ApplicationState state = ApplicationState.Idle;
		
		out = state.isOneOf(
				ConnectionState.Disconnected,
				ApplicationState.Idle,
				ProcessState.Before);
		
		assertTrue(out);
	}
}
