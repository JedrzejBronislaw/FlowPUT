package jedrzejbronislaw.flowmeasure.view.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jedrzejbronislaw.flowmeasure.tools.Delay;
import lombok.Setter;

public class DialogPaneController implements Initializable{

	@Setter
	private Node node;
	
	@Setter
	private Pane root;
	
	@FXML
	private Label title, content;
	
	public void show(String title, String content, int closeDelay) {
		setMessage(title, content);
		Platform.runLater(this::show);
		close(closeDelay);
	}
	
	private void setMessage(String title, String content) {
		this.title.setText(title);
		this.content.setText(content);
	}

	private void close(int delay) {
		Delay.viewAction(delay, this::close);
	}

	public void show() {
		root.getChildren().add(node);
	}

	public void close() {
		((StackPane)node.getParent()).getChildren().remove(node);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
}
