package jedrzejbronislaw.flowmeasure.view;

public interface ActionContainer {
	void startProcess();
	void endProcess();
	void saveProcess();
	void closeProcess();

	void connectFlowDevice();
	void disconnectFlowDevice();
	void autoconnectFlowDevice();
	
	void exit();
}
