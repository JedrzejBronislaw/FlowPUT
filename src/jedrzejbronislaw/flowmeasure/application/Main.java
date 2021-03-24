package jedrzejbronislaw.flowmeasure.application;
	
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) {
		Components.create(stage);
	}
	
	public static void main(String[] args) {
		launch(args);		
	}
}
