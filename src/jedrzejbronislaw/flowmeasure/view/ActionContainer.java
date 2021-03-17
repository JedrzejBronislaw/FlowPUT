package jedrzejbronislaw.flowmeasure.view;

public interface ActionContainer {
	void startProcess();
	void endProcess();
	void saveProcess();
	void closeProcess();

	void connectDevice();
	void disconnectDevices();
	void autoconnectDevice();
	
	void exit();
}
