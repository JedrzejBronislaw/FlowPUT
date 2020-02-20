package jedrzejbronislaw.flowmeasure;

import java.time.LocalDateTime;

public interface FlowMeasurementModel extends FlowMeasurementConsumer {

	void addFlowMeasurement(LocalDateTime time, int[] pulses);

	int getSize();
}
