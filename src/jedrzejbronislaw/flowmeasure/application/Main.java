package jedrzejbronislaw.flowmeasure.application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.states.StateManager;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) {
		
		Components.create(stage);
		
		createStateAndEventListeners();
	}

	private void createStateAndEventListeners() {
		StateManager stateManager = Components.getStateManager();
		EventManager eventManager = Components.getEventManager();
		
		stateManager.getAppState()    .addStateListener(state -> System.out.println(" -> New appState: "     + state.toString()));
		stateManager.getConnState()   .addStateListener(state -> System.out.println(" -> New connState: "    + state.toString()));
		stateManager.getProcessState().addStateListener(state -> System.out.println(" -> New processState: " + state.toString()));
		
		eventManager.addListener(event -> {
			if(event != EventType.RECEIVED_DATA) System.out.println(" -> Event: " + event.toString());
		});
	}
	
	public static void main(String[] args) {
		launch(args);		
	}
}
