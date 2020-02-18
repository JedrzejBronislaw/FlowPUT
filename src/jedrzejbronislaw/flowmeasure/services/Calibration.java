package jedrzejbronislaw.flowmeasure.services;

import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.FlowMeasurementConsumer;

public interface Calibration extends FlowMeasurementConsumer{

	void setFlowmeter(int flowmeter);
	void reset();
	void setVolume(float volumeInLiters);
	void newMeasurment();
	float getValue();
	
	void setValueListener(Consumer<Integer> valueListener);
}
