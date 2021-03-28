package jedrzejbronislaw.flowmeasure.view.deviceView;

import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;

public abstract class ViewFactory {
	
	protected EventManager   eventManager;
	private   StateManager   stateManager;
	protected ActionContainer actions;
	
	
	abstract public Node createLivePane();
	abstract public Node createTablePane();
	abstract public Node createChartPane();
	abstract public Node createSettingsPane();
	abstract public Node createCalibrationPane();
	
	
	public ViewFactory() {
		Components.getComponentsLoader().addLoadMethod(() -> {
			actions        = Components.getGlobalActions();
			eventManager   = Components.getEventManager();
			stateManager   = Components.getStateManager();
		});
	}
	
	
	protected void addAllStatesListener(AllStatesListener listener) {
		new AllStates(Components.getStateManager(), listener);
	}
	
	protected void addAppListener(StateListener<ApplicationState> listener) {
		stateManager.getAppState().addStateListener(listener);
	}
}
