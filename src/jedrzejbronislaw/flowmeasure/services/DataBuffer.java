package jedrzejbronislaw.flowmeasure.services;

import jedrzejbronislaw.flowmeasure.FlowMeasurementConsumer;

public interface DataBuffer extends FlowMeasurementConsumer {
	void newFlows(int[] flows);
	void newFlow(int flow);

	void setInterval(int interal);
	int getInterval();
}
