package jedrzejbronislaw.flowmeasure.services;

import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.FlowMeasurementConsumer;

public interface Calibration extends FlowMeasurementConsumer{

	void setFlowmeter(int flowmeter);
	void setVolume(float volumeInLiters);
	float getValue();
	void reset();
	void newMeasurment();
	
	void setValueListener(Consumer<Integer> valueListener);
}
