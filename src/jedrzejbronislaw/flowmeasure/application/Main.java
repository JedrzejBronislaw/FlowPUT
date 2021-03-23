package jedrzejbronislaw.flowmeasure.application;
	
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.states.StateManager;

public class Main extends Application {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void start(Stage stage) {
		Components.create(stage);
		
		createStateAndEventListeners();
	}

	private void createStateAndEventListeners() {
		StateManager stateManager = Components.getStateManager();
		EventManager eventManager = Components.getEventManager();
		
		stateManager.getAppState()    .addStateListener(state -> log.info("-> New appState: {}",     state));
		stateManager.getConnState()   .addStateListener(state -> log.info("-> New connState: {}",    state));
		stateManager.getProcessState().addStateListener(state -> log.info("-> New processState: {}", state));
		
		eventManager.addListener(event -> {
			if(event != EventType.RECEIVED_DATA) log.info("-> Event: {}", event);
		});
	}
	
	public static void main(String[] args) {
		launch(args);		
	}
}
