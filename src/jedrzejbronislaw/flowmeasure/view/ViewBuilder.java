package jedrzejbronislaw.flowmeasure.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.dialogManager.DialogManager;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.view.connection.UARTParamsBuilder;
import jedrzejbronislaw.flowmeasure.view.dialog.DialogPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.factory.DeviceView;
import jedrzejbronislaw.flowmeasure.view.factory.DeviceType;
import jedrzejbronislaw.flowmeasure.view.factory.EDViewFactory;
import jedrzejbronislaw.flowmeasure.view.factory.FlowViewFactory;
import jedrzejbronislaw.flowmeasure.view.mainWindow.MainWindowBuilder;
import jedrzejbronislaw.flowmeasure.view.mainWindow.MainWindowController;
import jedrzejbronislaw.flowmeasure.view.sidePane.SidePaneBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewBuilder {
	
	private static final int WINDOW_HEIGHT = 500;
	private static final int WINDOW_WIDTH  = 900;
	private static final String WINDOW_TITLE   = "FlowmeterPUT";
	private static final String CSS_FILENAME   = "application.css";
	private static final String LOGO_FILE_NAME = "logo.png";

	private Map<DeviceType, DeviceView> deviceViews = new HashMap<>();
	
	@NonNull private Components components;
	@NonNull private ActionContainer actions;
	
	private Pane root;
	private MainWindowController mainWindowController;
	
	
	public void build() {
		root = (Pane) mainWindow();
		buildWindow(root);
		buildDialog();
	}
	
	private void buildDialog() {
		DialogPaneBuilder builder = new DialogPaneBuilder(root);
		builder.build();
		
		dialogManager().setShowMessage(builder.getController()::show);
	}
	
	private void buildWindow(Pane root) {
		try {
			
			Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
			scene.getStylesheets().add(resources().getResourcePath(CSS_FILENAME));
			
			primaryStage().getIcons().add(loadLogo());
			primaryStage().setScene(scene);
			primaryStage().setTitle(WINDOW_TITLE);
			primaryStage().setOnCloseRequest(this::closeApplication);
			primaryStage().show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeApplication(WindowEvent e) {
		if (appState() == ApplicationState.PROCESS &&
			!confirmCloseWithAlert()) {
			
			e.consume();
			return;
		}

		actions.exit();
		Platform.exit();
	}

	private Image loadLogo() {
		return new Image(resources().path(LOGO_FILE_NAME));
	}

	private boolean confirmCloseWithAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close");
		alert.setHeaderText("Are you sure you want to close the application?");
		if (processState() == ProcessState.ONGOING)
			alert.setContentText("The measurement is in progress.");
		if (processState() == ProcessState.FINISHED)
			alert.setContentText("The measurement is not closed.");
		
		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get() == ButtonType.OK;
	}
	
	public Node uart() {
		UARTParamsBuilder builder = new UARTParamsBuilder(actions);
		builder.build();
		
		viewMediator().setUartParamsGetter(builder.getController()::getParams);
		addConnListener(builder.getController());
		
		return builder.getNode();
	}
	
	public Node sidePane() {
		SidePaneBuilder builder = new SidePaneBuilder(actions);
		builder.build();
		
		addAllStatesListener(builder.getController());
		addEventListener(builder.getController());
		
		return builder.getNode();
	}

	private Node mainWindow() {
		MainWindowBuilder builder = new MainWindowBuilder();
		builder.build();

		deviceViews.put(DeviceType.FlowDevice, new DeviceView(new FlowViewFactory(components, actions)));
		deviceViews.put(DeviceType.EDDevice,   new DeviceView(new   EDViewFactory(components, actions)));
		
		builder.getController().getBorderPane().setLeft(sidePane());
		builder.getController().getBorderPane().setRight(uart());

		addAppListener(builder.controller);
		
		mainWindowController = builder.getController();
		
		return builder.getNode();
	}

	public void setDeviceView(DeviceType device) {
		DeviceView view = deviceViews.get(device);
		if (view == null) return;
		
		Platform.runLater(() -> {
			mainWindowController.setLivePane       (view.getLivePane());
			mainWindowController.setTablePane      (view.getTablePane());
			mainWindowController.setChartPane      (view.getChartPane());
			mainWindowController.setSettingsPane   (view.getSettingsPane());
			mainWindowController.setCalibrationPane(view.getCalibrationPane());
		});
	}

	
	protected void addEventListener(EventListener listener) {
		eventManager().addListener(listener);
	}
	
	protected void addAllStatesListener(AllStatesListener listener) {
		new AllStates(components.getStateManager(), listener);
	}

	private void addAppListener(StateListener<ApplicationState> listener) {
		stateManager().getAppState().addStateListener(listener);
	}
	
	protected void addConnListener(StateListener<ConnectionState> listener) {
		stateManager().getConnState().addStateListener(listener);
	}

	private ApplicationState appState() {
		return stateManager().getAppState().getState();
	}

	private ProcessState processState() {
		return stateManager().getProcessState().getState();
	}
	
	private Stage primaryStage() {
		return components.getPrimaryStage();
	}
	
	private ResourceAccess resources() {
		return components.getResources();
	}
	
	protected EventManager eventManager() {
		return components.getEventManager();
	}
	
	private StateManager stateManager() {
		return components.getStateManager();
	}
	
	private DialogManager dialogManager() {
		return components.getDialogManager();
	}
	
	protected ViewMediator viewMediator() {
		return components.getViewMediator();
	}
}
