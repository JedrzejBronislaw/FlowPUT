package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

public interface ProcessRepositoryWriter extends SaveAction {

	void setPulsePerLitre(float[] pulsePerLitre);
	void setBufferInterval(int interval);
}
