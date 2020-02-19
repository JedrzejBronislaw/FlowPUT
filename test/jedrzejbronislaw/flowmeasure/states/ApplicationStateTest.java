package jedrzejbronislaw.flowmeasure.states;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationStateTest {

	@Test
	public void testCompare() {
		boolean out;
		ApplicationState state = ApplicationState.Process;
		
		out = state.compare().isOneOf(
				ApplicationState.Calibration,
				ApplicationState.Process);
		
		assertTrue(out);
	}

}
