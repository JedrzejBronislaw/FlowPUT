package jedrzejbronislaw.flowmeasure.view;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.SavingService;
import jedrzejbronislaw.flowmeasure.components.SettingsService;
import jedrzejbronislaw.flowmeasure.components.connectionMonitor.ConnectionMonitor;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.model.Repository;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryCSVWriter;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriter;
import jedrzejbronislaw.flowmeasure.settings.AppProperties;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.RatioProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTDevice;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.ConnectionAttempt;
import jedrzejbronislaw.flowmeasure.tools.uart.connection.MultiDeviceAutoConnection;
import jedrzejbronislaw.flowmeasure.view.saveWindow.SaveWindow;

public class Actions implements ActionContainer {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	

	@Override
	public void startProcess() {
		if (eventManager().submitEvent(EventType.PROCESS_STARTS)) {
			repository().createNewProcessRepository("").setStartWithNextValueFlag();
			
			if (isBufferedData())
				flowManager().setFlowConsumerType(FlowConsumerType.BUFFERED); else
				flowManager().setFlowConsumerType(FlowConsumerType.PLAIN);
		}
	}

	@Override
	public void endProcess() {
		if (eventManager().submitEvent(EventType.PROCESS_ENDS)) {
			repository().getCurrentProcessRepository().setProcessEndTimeNow();
			flowManager().setFlowConsumerType(FlowConsumerType.NONE);
		}
	}

	@Override
	public void saveProcess() {
		if (eventManager().submitEvent(EventType.SAVING_PROCESS))
			showSaveWindow(prepareWriter());
	}
	
	@Override
	public void closeProcess() {
		if (confirmWithAlert() && eventManager().submitEvent(EventType.CLOSE_PROCESS))
			repository().closeCurrentProcessRepository();
	}

	@Override
	public void connectDevice() {
		UARTParams params = viewMediator().getUARTParams();
		if (!valideteParams(params)) return;
		
		ConnectionAttempt attempt = connectionService().createConnectionAttempt(edDevice(), params);
		eventManager().submitEvent(EventType.CONNECTING_START);
		
		attempt.start();
	}

	@Override
	public void disconnectDevices() {
		connectionMonitor().stop();
		devices().forEach(d -> d.disconnect());

		eventManager().submitEvent(EventType.DISCONNECTION);
	}

	@Override
	public void autoconnectDevice() {
		log.info("Start autoconnect");
		
		MultiDeviceAutoConnection autoConnection = connectionService().createMultiDeviceAutoConnection();

		eventManager().submitEvent(EventType.CONNECTING_START);
		autoConnection.start();
	}

	@Override
	public void exit() {
		eventManager().submitEvent(EventType.EXITING);
		connectionMonitor().stop();
		devices().forEach(d -> d.disconnect());
	}
	
	private ProcessRepositoryWriter prepareWriter() {
		ProcessRepositoryWriter writer = new ProcessRepositoryCSVWriter();
		
		if (isBufferedData())
			writer.setBufferInterval(settings().getInt(AppProperties.BUFFER_INTERVAL));
		writer.setPulsePerLitre(getPulseRatios());
		writer.setFlowmeterNames(getFlowmeterNames());
		
		return writer;
	}

	private void showSaveWindow(ProcessRepositoryWriter writer) {
		ProcessRepository process = repository().getCurrentProcessRepository();
		SaveWindow saveWindow = new SaveWindow(resources(), process, settingsService(), savingService());

		saveWindow.setOwner(Components.getPrimaryStage());
		saveWindow.setSaveAction(writer::save);
			
		saveWindow.showWindow();
	}

	private float[] getPulseRatios() {
		RatioProperty[] ratioProperties = RatioProperty.generate(Consts.FLOWMETERS_NUMBER);
		float[] outcome = new float[Consts.FLOWMETERS_NUMBER];
		
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			outcome[i] = settings().getFloat(ratioProperties[i]);
		
		return outcome;
	}

	private String[] getFlowmeterNames() {
		FlowmeterNameProperty[] ratioProperties = FlowmeterNameProperty.generate(Consts.FLOWMETERS_NUMBER);
		String[] outcome = new String[Consts.FLOWMETERS_NUMBER];
		
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			outcome[i] = settings().getString(ratioProperties[i]);
		
		return outcome;
	}

	private boolean confirmWithAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close the process");
		alert.setHeaderText("Are you sure you want to close the process?");
		alert.setContentText("The measurement will be lost.");
		
		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get() == ButtonType.OK;
	}

	private boolean valideteParams(UARTParams params) {
		if (params == null) return false;
		if (params.PORT_NAME == null || params.PORT_NAME.isEmpty()) return false;
		
		return true;
	}

	private boolean isBufferedData() {
		return settings().getBool(AppProperties.BUFFERED_DATA);
	}
	
	
	private EventManager eventManager() {
		return Components.getEventManager();
	}
	
	private ConnectionMonitor connectionMonitor() {
		return Components.getConnectionMonitor();
	}
	
	private UARTDevice edDevice() {
		return Components.getEdDevice();
	}
	
	private List<UARTDevice> devices() {
		return Components.getDevices();
	}
	
	private ViewMediator viewMediator() {
		return Components.getViewMediator();
	}
	
	private ResourceAccess resources() {
		return Components.getResources();
	}
	
	private FlowManager flowManager() {
		return Components.getFlowManager();
	}
	
	private Settings settings() {
		return Components.getSettings();
	}
	
	private SettingsService settingsService() {
		return Components.getSettingsService();
	}
	
	private SavingService savingService() {
		return Components.getSavingService();
	}
	
	private Repository repository() {
		return Components.getRepository();
	}
	
	private ConnectionService connectionService() {
		return Components.getConnectionService();
	}
}
