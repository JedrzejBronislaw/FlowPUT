package jedrzejbronislaw.flowmeasure.application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.FakeProcessGenerator1;
import jedrzejbronislaw.flowmeasure.events.EventType;


public class Main extends Application {
	
	private Components components;

	@Override
	public void start(Stage stage) {
		
		components = new Components(stage);
		
		//-----
		
		components.getStateManager().getAppState().addStateListiner(state -> System.out.println(" -> New appState: " + state.toString()));
		components.getStateManager().getConnState().addStateListiner(state -> System.out.println(" -> New connState: " + state.toString()));
		components.getStateManager().getProcessState().addStateListiner(state -> System.out.println(" -> New processState: " + state.toString()));
		components.getEventManager().addListener(state -> {if(state != EventType.ReceivedData) System.out.println(" -> Event: " + state.toString());});
		
//		generateFakeData();
	}
	
	private void generateFakeData() {
		FakeProcessGenerator1 generator = new FakeProcessGenerator1();
		generator.setInterval(1000);
		generator.setNumOfFlowmeters(6);
		
		generator.generate(components.getRepository().getCurrentProcessRepository(), 10*60*60);
	}
	
	public static void main(String[] args) {
		launch(args);		
	}
}
