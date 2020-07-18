package jedrzejbronislaw.flowmeasure.services;

import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class Calibration1 implements Calibration{

	@Setter private int flowmeter;
	@Getter private float value = 0;
	@Setter private float volume = 0;

	@Setter private Consumer<Integer> valueListener = null;

	
	public Calibration1(int flowmeter) {
		this.flowmeter = flowmeter;
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		value += selectFlow(pulses);
		Injection.run(valueListener, (int)value);
	}

	private int selectFlow(int[] pulses) {
		try {
			return pulses[flowmeter-1];
		} catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	@Override
	public void reset() {
		value = 0;
		Injection.run(valueListener, (int)value);
	}

	@Override
	public void newMeasurment() {
		reset();
	}
}
