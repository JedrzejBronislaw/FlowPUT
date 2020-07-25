package jedrzejbronislaw.flowmeasure.tools;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class MyFXMLLoader<T extends Initializable> {

	@RequiredArgsConstructor
	public static class NodeAndController<T>{
		@NonNull @Getter private Node node;
		@NonNull @Getter private T controller;
	}
	
	@Setter static private ResourceAccess resources;

	
	public NodeAndController<T> create(String fxmlFilePath) {
		FXMLLoader fxmlLoader = new FXMLLoader();

		fxmlLoader.setLocation(resources.getResourceURL(fxmlFilePath));

    	Node node;
		try {
			node = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    	
		return new NodeAndController<T>(node, fxmlLoader.getController());
	}
}
