package jedrzejbronislaw.flowmeasure.application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.events.EventType;
import jedrzejbronislaw.flowmeasure.states.StateManager;


public class Main extends Application {
	
	private Components components;

	@Override
	public void start(Stage stage) {
		
		components = new Components(stage);
		
		createStateAndEventListeners();
	}

	private void createStateAndEventListeners() {
		StateManager stateManager = components.getStateManager();
		EventManager eventManager = components.getEventManager();
		
		stateManager.getAppState()    .addStateListiner(state -> System.out.println(" -> New appState: "     + state.toString()));
		stateManager.getConnState()   .addStateListiner(state -> System.out.println(" -> New connState: "    + state.toString()));
		stateManager.getProcessState().addStateListiner(state -> System.out.println(" -> New processState: " + state.toString()));
		
		eventManager.addListener(event -> {
			if(event != EventType.ReceivedData) System.out.println(" -> Event: " + event.toString());
		});
	}
	
	public static void main(String[] args) {
		launch(args);		
	}
}
