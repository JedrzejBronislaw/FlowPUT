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
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.view.dialog.DialogPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.factory.EDViewFactory;
import jedrzejbronislaw.flowmeasure.view.factory.ViewFactory;
import jedrzejbronislaw.flowmeasure.view.mainWindow.MainWindowBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewBuilder {
	
	private static final int WINDOW_HEIGHT = 500;
	private static final int WINDOW_WIDTH  = 900;
	private static final String WINDOW_TITLE   = "FlowmeterPUT";
	private static final String CSS_FILENAME   = "application.css";
	private static final String LOGO_FILE_NAME = "logo.png";

	@NonNull private Components components;
	@NonNull private ActionContainer actions;
	
	private Pane root;
	
	
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

	private Node mainWindow() {
		MainWindowBuilder builder = new MainWindowBuilder();
		builder.build();
		
		ViewFactory viewFactory = new EDViewFactory(components, actions);
		
		builder.getController().getBorderPane().setLeft(viewFactory.sidePane());
		builder.getController().getBorderPane().setRight(viewFactory.uart());
		builder.getController().setLivePane(viewFactory.livePane());
		builder.getController().setTablePane(viewFactory.table());
		builder.getController().setChartPane(viewFactory.chart());
		builder.getController().setSettingsPane(viewFactory.settingsPane());
		builder.getController().setCalibrationPane(viewFactory.calibrationPane());

		addAppListener(builder.controller);
		
		return builder.getNode();
	}


	private void addAppListener(StateListener<ApplicationState> listener) {
		stateManager().getAppState().addStateListener(listener);
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
	
	private StateManager stateManager() {
		return components.getStateManager();
	}
	
	private DialogManager dialogManager() {
		return components.getDialogManager();
	}
}
