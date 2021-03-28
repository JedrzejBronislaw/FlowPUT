package jedrzejbronislaw.flowmeasure.view;

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
import jedrzejbronislaw.flowmeasure.devices.DeviceType;
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
import jedrzejbronislaw.flowmeasure.view.connection.ConnectionPane;
import jedrzejbronislaw.flowmeasure.view.deviceView.DeviceView;
import jedrzejbronislaw.flowmeasure.view.deviceView.EDViewFactory;
import jedrzejbronislaw.flowmeasure.view.deviceView.FlowViewFactory;
import jedrzejbronislaw.flowmeasure.view.dialog.DialogPane;
import jedrzejbronislaw.flowmeasure.view.mainWindow.MainWindow;
import jedrzejbronislaw.flowmeasure.view.sidePane.SidePane;

public class ViewBuilder {
	
	private static final int WINDOW_HEIGHT = 500;
	private static final int WINDOW_WIDTH  = 900;
	private static final String WINDOW_TITLE   = "FlowmeterPUT";
	private static final String CSS_FILENAME   = "application.css";
	private static final String LOGO_FILE_NAME = "logo.png";

	private MainWindow mainWindow;
	
	private   Stage          primaryStage;
	private   ResourceAccess resources;
	protected EventManager   eventManager;
	private   StateManager   stateManager;
	private   DialogManager  dialogManager;
	protected ViewMediator   viewMediator;
	private   ViewManager    viewManager;
	private   ActionContainer actions;
	
	
	public ViewBuilder() {
		Components.getComponentsLoader().addLoadMethod(() -> {
			primaryStage  = Components.getPrimaryStage();
			resources     = Components.getResources();
			eventManager  = Components.getEventManager();
			stateManager  = Components.getStateManager();
			dialogManager = Components.getDialogManager();
			viewMediator  = Components.getViewMediator();
			viewManager   = Components.getViewManager();
			actions       = Components.getGlobalActions();
		});
	}
	
	
	public void build() {
		mainWindow = mainWindow();
		buildWindow(mainWindow);
		buildDialog();
	}
	
	private void buildDialog() {
		DialogPane dialogPane = new DialogPane(mainWindow);
		
		dialogManager.setShowMessage(dialogPane::show);
	}
	
	private void buildWindow(Pane root) {
		try {
			
			Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
			scene.getStylesheets().add(resources.getResourcePath(CSS_FILENAME));
			
			primaryStage.getIcons().add(loadLogo());
			primaryStage.setScene(scene);
			primaryStage.setTitle(WINDOW_TITLE);
			primaryStage.setOnCloseRequest(this::closeApplication);
			primaryStage.show();
			
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
		return new Image(resources.path(LOGO_FILE_NAME));
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
	
	private Node connectionPane() {
		ConnectionPane connectionPane = new ConnectionPane();

		viewMediator.setUartParamsGetter(connectionPane::getParams);
		addConnListener(connectionPane);
		
		return connectionPane;
	}
	
	private Node sidePane() {
		SidePane sidePane = new SidePane();
		
		addAllStatesListener(sidePane);
		addEventListener(sidePane);
		
		return sidePane;
	}

	private MainWindow mainWindow() {
		MainWindow mainWindow = new MainWindow();

		viewManager.setMainWindow(mainWindow);
		viewManager.addDeviceView(DeviceType.FlowDevice, new DeviceView(new FlowViewFactory()));
		viewManager.addDeviceView(DeviceType.EDDevice,   new DeviceView(new   EDViewFactory()));
		
		mainWindow.getBorderPane().setLeft(sidePane());
		mainWindow.getBorderPane().setRight(connectionPane());

		addAppListener(mainWindow);
		
		return mainWindow;
	}

	
	private void addEventListener(EventListener listener) {
		eventManager.addListener(listener);
	}
	
	private void addAllStatesListener(AllStatesListener listener) {
		new AllStates(Components.getStateManager(), listener);
	}

	private void addAppListener(StateListener<ApplicationState> listener) {
		stateManager.getAppState().addStateListener(listener);
	}
	
	private void addConnListener(StateListener<ConnectionState> listener) {
		stateManager.getConnState().addStateListener(listener);
	}

	private ApplicationState appState() {
		return stateManager.getAppState().getState();
	}

	private ProcessState processState() {
		return stateManager.getProcessState().getState();
	}
}
