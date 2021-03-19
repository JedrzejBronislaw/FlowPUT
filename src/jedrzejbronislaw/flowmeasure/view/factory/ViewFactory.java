package jedrzejbronislaw.flowmeasure.view.factory;

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
	
	@NonNull protected Components components;
	@NonNull protected ActionContainer actions;
	
	
	abstract public Node livePane();
	abstract public Node table();
	abstract public Node chart();
	abstract public Node settingsPane();
	abstract public Node calibrationPane();
	
	
	protected void addEventListener(EventListener listener) {
		eventManager().addListener(listener);
	}
	
	protected void addAllStatesListener(AllStatesListener listener) {
		new AllStates(components.getStateManager(), listener);
	}
	
	protected void addAppListener(StateListener<ApplicationState> listener) {
		stateManager().getAppState().addStateListener(listener);
	}
	
	protected void addConnListener(StateListener<ConnectionState> listener) {
		stateManager().getConnState().addStateListener(listener);
	}
	
	protected ProcessRepository getCurrentProcessRepo() {
		return components.getRepository().getCurrentProcessRepository();
	}
	
	protected FlowManager flowManager() {
		return components.getFlowManager();
	}
	
	protected Settings settings() {
		return components.getSettings();
	}
	
	protected EventManager eventManager() {
		return components.getEventManager();
	}
	
	private StateManager stateManager() {
		return components.getStateManager();
	}
	
	protected Calibration calibration() {
		return components.getCalibration();
	}
	
	protected FlowConverters flowconverters() {
		return components.getFlowConverters();
	}
	
	protected ViewMediator viewMediator() {
		return components.getViewMediator();
	}
}
