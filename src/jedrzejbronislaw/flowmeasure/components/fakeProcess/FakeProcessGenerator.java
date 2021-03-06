package jedrzejbronislaw.flowmeasure.components.fakeProcess;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public interface FakeProcessGenerator {
	
	FakeProcessGenerator setNumOfFlowmeters(int n);
	FakeProcessGenerator setInterval(int interval);
	
	void generate(ProcessRepository model, long size);
}
