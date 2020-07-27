package jedrzejbronislaw.flowmeasure.states;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationStateTest {

	@Test
	public void test_isOneOf_true() {
		boolean out;
		ApplicationState state = ApplicationState.PROCESS;
		
		out = state.isOneOf(
				ApplicationState.CALIBRATION,
				ApplicationState.PROCESS);
		
		assertTrue(out);
	}

	@Test
	public void test_isOneOf_false() {
		boolean out;
		ApplicationState state = ApplicationState.PROCESS;
		
		out = state.isOneOf(
				ApplicationState.CALIBRATION,
				ApplicationState.IDLE);
		
		assertFalse(out);
	}

	@Test
	public void test_isOneOf_otherStateTypes_false() {
		boolean out;
		ApplicationState state = ApplicationState.IDLE;
		
		out = state.isOneOf(
				ConnectionState.DISCONNECTED,
				ApplicationState.CALIBRATION,
				ProcessState.BEFORE);
		
		assertFalse(out);
	}

	@Test
	public void test_isOneOf_otherStateTypes_true() {
		boolean out;
		ApplicationState state = ApplicationState.IDLE;
		
		out = state.isOneOf(
				ConnectionState.DISCONNECTED,
				ApplicationState.IDLE,
				ProcessState.BEFORE);
		
		assertTrue(out);
	}
}
