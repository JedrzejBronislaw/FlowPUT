package jedrzejbronislaw.flowmeasure.view;

public interface ActionContainer {
	void startProcess();
	void endProcess();
	void saveProcess();

	void connectFlowDevice();
	void disconnectFlowDevice();
	void autoconnectFlowDevice();
	
	void exit();
}
