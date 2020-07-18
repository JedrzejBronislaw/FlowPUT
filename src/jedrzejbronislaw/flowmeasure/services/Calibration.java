package jedrzejbronislaw.flowmeasure.services;

import java.util.List;
import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.FlowMeasurementConsumer;

public interface Calibration extends FlowMeasurementConsumer{

	void setFlowmeter(int flowmeter);
	void setVolume(float volumeInLiters);
	float getAveValue();
	void reset();
	void newMeasure();
	
	void setAveValueListener(Consumer<Float>         valueListener);
	void setValuesListener  (Consumer<List<Integer>> valueListener);
}
