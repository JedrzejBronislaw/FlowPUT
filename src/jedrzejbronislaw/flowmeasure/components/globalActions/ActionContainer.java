package jedrzejbronislaw.flowmeasure.components.globalActions;

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
