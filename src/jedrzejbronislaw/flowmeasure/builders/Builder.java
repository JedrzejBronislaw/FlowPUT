package jedrzejbronislaw.flowmeasure.builders;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader.NodeAndController;
import lombok.Getter;

public abstract class Builder<T extends Initializable> {

	@Getter protected Node node;
	@Getter protected T controller;
	
	
	public void build() {
		MyFXMLLoader<T> loader = new MyFXMLLoader<>();
		NodeAndController<T> nac = loader.create(getFxmlFilePath());
		controller = nac.getController();
		node = nac.getNode();
		
		afterBuild();
	}
	
	abstract void afterBuild();
	abstract String getFxmlFilePath();
}
