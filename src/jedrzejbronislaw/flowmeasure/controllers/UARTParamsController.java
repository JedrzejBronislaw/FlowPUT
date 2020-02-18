package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import jedrzejbronislaw.flowmeasure.UARTParams;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import lombok.Setter;

public class UARTParamsController implements Initializable, StateListener<ConnectionState> {

	@FXML
	private ComboBox<String> ports;

	@FXML
	private Button connectButton;
	@FXML
	private Button disconnectButton;
	@FXML
	private Button refreshPortsButton;
	@FXML
	private Button autoConnectButton;

	@Setter
	private Runnable connectButtonAction;
	@Setter
	private Runnable disconnectButtonAction;
	@Setter
	private Runnable refreshPortsButtonAction;
	@Setter
	private Runnable autoConnectButtonAction;
	
	public UARTParams getParams() {
		UARTParams params = new UARTParams();
		
		params.PORT_NAME = ports.getSelectionModel().getSelectedItem();
//		params.DATA_RATE = Integer.parseInt(rates.getSelectionModel().getSelectedItem());
		
		return params;
	}

	public void setDisableFields(boolean disable) {
		ports.setDisable(disable);

		connectButton.setDisable(disable);
		disconnectButton.setDisable(disable);
		autoConnectButton.setDisable(disable);
	}
	
	public void setPortsNames(List<String> portsNames) {
		ports.getItems().clear();
		ports.getItems().addAll(portsNames);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setDisableFields(false);

		connectButton.setOnAction(e -> {
			if(connectButtonAction != null)
				connectButtonAction.run();
		});
		
		disconnectButton.setOnAction(e -> {
			if(disconnectButtonAction != null)
				disconnectButtonAction.run();
		});
		
		refreshPortsButton.setOnAction(e -> {
			if(refreshPortsButtonAction != null)
				refreshPortsButtonAction.run();
		});
		
		autoConnectButton.setOnAction(e -> {
			if(autoConnectButtonAction != null)
				autoConnectButtonAction.run();
		});
	}

	@Override
	public void onChangeState(ConnectionState state) {
		if (state == ConnectionState.Connecting)
			setDisableFields(true);
		else if (state == ConnectionState.Connected) {
			setDisableFields(true);
			disconnectButton.setDisable(false);
		} else if (state == ConnectionState.Disconnected) {
			setDisableFields(false);
			disconnectButton.setDisable(true);
		}
	}
}
