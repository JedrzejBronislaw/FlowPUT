package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.calibration.Calibration;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ViewFactory {
	
	@NonNull protected ActionContainer actions;
	
	
	abstract public Node createLivePane();
	abstract public Node createTablePane();
	abstract public Node createChartPane();
	abstract public Node createSettingsPane();
	abstract public Node createCalibrationPane();
	
	
	protected void addEventListener(EventListener listener) {
		eventManager().addListener(listener);
	}
	
	protected void addAllStatesListener(AllStatesListener listener) {
		new AllStates(Components.getStateManager(), listener);
	}
	
	protected void addAppListener(StateListener<ApplicationState> listener) {
		stateManager().getAppState().addStateListener(listener);
	}
	
	protected void addConnListener(StateListener<ConnectionState> listener) {
		stateManager().getConnState().addStateListener(listener);
	}
	
	protected ProcessRepository getCurrentProcessRepo() {
		return Components.getRepository().getCurrentProcessRepository();
	}
	
	protected FlowManager flowManager() {
		return Components.getFlowManager();
	}
	
	protected Settings settings() {
		return Components.getSettings();
	}
	
	protected EventManager eventManager() {
		return Components.getEventManager();
	}
	
	private StateManager stateManager() {
		return Components.getStateManager();
	}
	
	protected Calibration calibration() {
		return Components.getCalibration();
	}
	
	protected FlowConverters flowconverters() {
		return Components.getFlowConverters();
	}
	
	protected ViewMediator viewMediator() {
		return Components.getViewMediator();
	}
}