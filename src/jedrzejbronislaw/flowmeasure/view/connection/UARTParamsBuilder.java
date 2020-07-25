package jedrzejbronislaw.flowmeasure.view.connection;

import jedrzejbronislaw.flowmeasure.flowDevice.UART;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UARTParamsBuilder extends Builder<UARTParamsController> {

	@Getter private String fxmlFilePath = "UARTParams.fxml";

	private final ActionContainer actions;
	
	public void afterBuild() {
		controller.setPortsNames(UART.getPortList());
		controller.setPortsSupplier(UART::getPortList);

		controller.setConnectButtonAction(    actions::connectFlowDevice);
		controller.setDisconnectButtonAction( actions::disconnectFlowDevice);
		controller.setAutoConnectButtonAction(actions::autoconnectFlowDevice);
	}
}
