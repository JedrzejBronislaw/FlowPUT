package jedrzejbronislaw.flowmeasure.view.connection;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import jedrzejbronislaw.flowmeasure.tools.TextTools;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import jedrzejbronislaw.flowmeasure.tools.uart.UARTParams;
import lombok.Setter;

public class UARTParamsController implements Initializable, StateListener<ConnectionState> {

	@FXML private ComboBox<String> ports;

	@FXML private Button connectButton;
	@FXML private Button disconnectButton;
	@FXML private Button autoConnectButton;
	@FXML private Label statusLabel, verticalStatusLabel;
	@FXML private Shape hideShape;
	@FXML private VBox controlPane;

	@Setter private Runnable connectButtonAction;
	@Setter private Runnable disconnectButtonAction;
	@Setter private Runnable autoConnectButtonAction;
	
	@Setter private Supplier<List<String>> portsSupplier;
	
	private BooleanProperty hidden = new SimpleBooleanProperty(false);
	
	
	public UARTParams getParams() {
		UARTParams params = new UARTParams();
		
		params.PORT_NAME = ports.getSelectionModel().getSelectedItem();
		
		return params;
	}
	
	public void setPortsNames(List<String> portsNames) {
		if (portsNames == null) return;
		
		ports.getItems().clear();
		ports.getItems().addAll(portsNames);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		connectButton.setOnAction(e      -> Injection.run(connectButtonAction));
		disconnectButton.setOnAction(e   -> Injection.run(disconnectButtonAction));
		autoConnectButton.setOnAction(e  -> Injection.run(autoConnectButtonAction));
		
		ports.setOnShowing(e -> setPortsNames(Injection.get(portsSupplier)));
		
		hideShape          .setOnMouseClicked(e -> hideShow());
		verticalStatusLabel.setOnMouseClicked(e -> hideShow());
		
		verticalStatusLabel.textProperty().bind(statusLabel.textProperty());
		controlPane.managedProperty().bind(hidden.not());
		controlPane.visibleProperty().bind(hidden.not());
		verticalStatusLabel.managedProperty().bind(hidden);
		verticalStatusLabel.visibleProperty().bind(hidden);
	}

	
	private void hideShow() {
		hidden.set(!hidden.get());
		hideShape.setRotate(hidden.get() ? 180 : 0);
	}

	private void setEnable(Node node, boolean enable) {
		node.setDisable(!enable);
	}

	@Override
	public void onChangeState(ConnectionState state) {
		Platform.runLater(() ->
			statusLabel.setText(TextTools.firstCharUpper(state.toString())));
		
		setEnable(ports,             state == ConnectionState.DISCONNECTED);
		
		setEnable(connectButton,     state == ConnectionState.DISCONNECTED);
		setEnable(disconnectButton,  state == ConnectionState.CONNECTED);
		setEnable(autoConnectButton, state == ConnectionState.DISCONNECTED);
	}
}
