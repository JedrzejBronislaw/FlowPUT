package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

public interface ProcessRepositoryWriter extends SaveAction {

	void setPulsePerLitre(float[] pulsePerLitre);
	void setFlowmeterNames(String[] names);
	void setBufferInterval(int interval);
}
