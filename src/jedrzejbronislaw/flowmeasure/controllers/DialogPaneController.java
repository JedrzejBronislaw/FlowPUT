package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import jedrzejbronislaw.flowmeasure.tools.Delay;
import lombok.Setter;

public class DialogPaneController implements Initializable{

	@Setter
	private Node node;
	
	@FXML
	private Label title, content;
	
	public void setMessage(String title, String content) {
		this.title.setText(title);
		this.content.setText(content);
	}

	public void close(int delay) {
		Delay.viewAction(delay,() -> ((StackPane)node.getParent()).getChildren().remove(node));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	
}
