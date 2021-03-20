package jedrzejbronislaw.flowmeasure.view.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jedrzejbronislaw.flowmeasure.tools.Delay;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;

public class DialogPane extends VBox implements Initializable {

	@FXML private Label title, content;
	
	private Pane root;

	
	public DialogPane(Pane root) {
		this.root = root;
		
		MyFXMLLoader2.create("DialogPane.fxml", this);
	}
	
	
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
		root.getChildren().add(this);
	}

	public void close() {
		((StackPane)getParent()).getChildren().remove(this);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY, Insets.EMPTY)));
	}
}
