package jedrzejbronislaw.flowmeasure.tools;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class MyFXMLLoader {

	private final static String MAIN_DIR = "/res";
//	private final static String lang = Internationalization.RESOURCE_LOCATION;
	
	@RequiredArgsConstructor
	public static class NodeAndController<T>{
		@NonNull @Getter private Node node;
		@NonNull @Getter private T controller;
		
		public Parent getParent() {
			return (Parent) node;
		}
		
		public Pane getPane() {
			return (Pane) node;
		}
	}
	
	
	private static String path(String resource) {
		return MAIN_DIR + "/"  + resource;
	}

	private URL url(String resource) {
		return getClass().getResource(path(resource));
	}
	
	public <T> NodeAndController<T> create(String fxmlFilePath) {
		FXMLLoader fxmlLoader = new FXMLLoader();

		fxmlLoader.setLocation(url(fxmlFilePath));
//		fxmlLoader.setResources(ResourceBundle.getBundle(lang));

    	Node node;
		try {
			node = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    	
		return new NodeAndController<T>(node, fxmlLoader.getController());
	}
	
	public static boolean create(String fxmlFilePath, Pane node) {
		FXMLLoader fxmlLoader = new FXMLLoader();
		node.getChildren().clear();

		fxmlLoader.setLocation(node.getClass().getResource(path(fxmlFilePath)));
//		fxmlLoader.setResources(ResourceBundle.getBundle(lang));
		
		fxmlLoader.setRoot(node);
		fxmlLoader.setController(node);
		
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
