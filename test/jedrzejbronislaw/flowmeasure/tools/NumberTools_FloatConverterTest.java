package jedrzejbronislaw.flowmeasure.tools;

import static org.junit.Assert.*;

import org.junit.Test;

import jedrzejbronislaw.flowmeasure.tools.NumberTools;

public class NumberTools_FloatConverterTest {

	@Test
	public void comma_1_5() {
		assertEquals("1,5", NumberTools.floatToString(1.5f, ","));
	}
	
	@Test
	public void dot_1_5() {
		assertEquals("1.5", NumberTools.floatToString(1.5f, "."));
	}

	@Test
	public void underscore_1_5() {
		assertEquals("1_5", NumberTools.floatToString(1.5f, "_"));
	}
	
	@Test
	public void questionMark_1_5() {
		assertEquals("1?5", NumberTools.floatToString(1.5f, "?"));
	}

	
	@Test
	public void underscore_0() {
		assertEquals("0_0", NumberTools.floatToString(0f, "_"));
	}

	@Test
	public void underscore_negative() {
		assertEquals("-1_5", NumberTools.floatToString(-1.5f, "_"));
	}

	@Test
	public void underscore_integer() {
		assertEquals("100_0", NumberTools.floatToString(100f, "_"));
	}
}
