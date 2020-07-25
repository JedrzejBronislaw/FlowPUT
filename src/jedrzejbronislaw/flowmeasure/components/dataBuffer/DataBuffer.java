package jedrzejbronislaw.flowmeasure.components.dataBuffer;

import jedrzejbronislaw.flowmeasure.components.flowManager.FlowMeasurementConsumer;

public interface DataBuffer extends FlowMeasurementConsumer {
	void newFlows(int[] flows);
	void newFlow(int flow);

	int getInterval();
}
