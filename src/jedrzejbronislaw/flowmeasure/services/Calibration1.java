package jedrzejbronislaw.flowmeasure.services;

import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.tools.Injection;

public class Calibration1 implements Calibration{

	int flowmeter;
	int value;
	float volume;

	private Consumer<Integer> valueListener = null;

	
	public Calibration1(int flowmeter) {
		this.flowmeter = flowmeter;
		this.value = 0;
		volume = 0;
	}


	@Override
	public void setValueListener(Consumer<Integer> valueListener) {
		this.valueListener = valueListener;
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		if(flowmeter > 0 && flowmeter <= pulses.length)
			value += pulses[flowmeter-1];
		
		Injection.run(valueListener, value);
	}

	@Override
	public void setFlowmeter(int flowmeter) {
		this.flowmeter = flowmeter;
	}
	
	@Override
	public void reset() {
		value = 0;
	}

	@Override
	public void setVolume(float volumeInLiters) {
		volume = volumeInLiters;
	}

	@Override
	public void newMeasurment() {
		reset();
	}

	@Override
	public float getValue() {
		return value;
	}

}
