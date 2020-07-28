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
import jedrzejbronislaw.flowmeasure.components.calibration.Calibration;
import jedrzejbronislaw.flowmeasure.components.dialogManager.DialogManager;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.components.flowManager.FlowManager;
import jedrzejbronislaw.flowmeasure.events.EventListener;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.states.AllStates;
import jedrzejbronislaw.flowmeasure.states.AllStatesListener;
import jedrzejbronislaw.flowmeasure.states.ApplicationState;
import jedrzejbronislaw.flowmeasure.states.ConnectionState;
import jedrzejbronislaw.flowmeasure.states.ProcessState;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.tools.observableState.StateListener;
import jedrzejbronislaw.flowmeasure.tools.resourceAccess.ResourceAccess;
import jedrzejbronislaw.flowmeasure.view.calibration.CalibrationPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.chart.ChartPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.connection.UARTParamsBuilder;
import jedrzejbronislaw.flowmeasure.view.dialog.DialogPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.live.LivePaneBuilder;
import jedrzejbronislaw.flowmeasure.view.mainWindow.MainWindowBuilder;
import jedrzejbronislaw.flowmeasure.view.settings.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.view.sidePane.SidePaneBuilder;
import jedrzejbronislaw.flowmeasure.view.table.MeasurementTableBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewBuilder {
	
	private static final int WINDOW_HEIGHT = 500;
	private static final int WINDOW_WIDTH  = 900;
	private static final String WINDOW_TITLE   = "FlowmeterPP";
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

	private Node uart() {
		UARTParamsBuilder builder = new UARTParamsBuilder(actions);
		builder.build();
		
		viewMediator().setUartParamsGetter(builder.getController()::getParams);
		addConnListener(builder.getController());
		
		return builder.getNode();
	}
	
	private Node chart() {
		ChartPaneBuilder builder = new ChartPaneBuilder(this::getCurrentProcessRepo, flowconverters());
		builder.build();
		
		return builder.getNode();
	}

	private Node table() {
		MeasurementTableBuilder builder = new MeasurementTableBuilder(this::getCurrentProcessRepo);
		builder.build();
		
		return builder.getNode();
	}
	
	private Node sidePane(){
		SidePaneBuilder builder = new SidePaneBuilder(actions);
		builder.build();
		
		addAllStatesListener(builder.getController());
		addEventListener(builder.getController());
		
		return builder.getNode();
	}
	
	private Node settingsPane(){
		SettingsPaneBuilder builder = new SettingsPaneBuilder(settings());
		builder.build();
		
		addAppListener(builder.getController());
		
		return builder.getNode();
	}

	private Node calibrationPane() {
		CalibrationPaneBuilder builder = new CalibrationPaneBuilder(eventManager(), flowManager(), settings(), calibration());
		builder.build();
		
		addAllStatesListener(builder.getController());
		
		return builder.getNode();
	}

	private Node livePane() {
		LivePaneBuilder builder = new LivePaneBuilder(viewMediator(), flowconverters());
		builder.build();
		
		return builder.getNode();
	}
	
	private Node mainWindow() {
		MainWindowBuilder builder = new MainWindowBuilder();
		builder.build();
		
		builder.getController().getBorderPane().setLeft(sidePane());
		builder.getController().getBorderPane().setRight(uart());
		builder.getController().setLivePane(livePane());
		builder.getController().setTablePane(table());
		builder.getController().setChartPane(chart());
		builder.getController().setSettingsPane(settingsPane());
		builder.getController().setCalibrationPane(calibrationPane());

		addAppListener(builder.controller);
		
		return builder.getNode();
	}


	private void addEventListener(EventListener listener) {
		eventManager().addListener(listener);
	}
	
	private void addAllStatesListener(AllStatesListener listener) {
		new AllStates(components.getStateManager(), listener);
	}
	
	private void addAppListener(StateListener<ApplicationState> listener) {
		stateManager().getAppState().addStateListener(listener);
	}
	
	private void addConnListener(StateListener<ConnectionState> listener) {
		stateManager().getConnState().addStateListener(listener);
	}

	private ApplicationState appState() {
		return stateManager().getAppState().getState();
	}

	private ProcessState processState() {
		return stateManager().getProcessState().getState();
	}
	
	private ProcessRepository getCurrentProcessRepo() {
		return components.getRepository().getCurrentProcessRepository();
	}
	
	private Stage primaryStage() {
		return components.getPrimaryStage();
	}
	
	private FlowManager flowManager() {
		return components.getFlowManager();
	}
	
	private Settings settings() {
		return components.getSettings();
	}
	
	private ResourceAccess resources() {
		return components.getResources();
	}
	
	private EventManager eventManager() {
		return components.getEventManager();
	}
	
	private StateManager stateManager() {
		return components.getStateManager();
	}
	
	private DialogManager dialogManager() {
		return components.getDialogManager();
	}
	
	private Calibration calibration() {
		return components.getCalibration();
	}
	
	private FlowConverters flowconverters() {
		return components.getFlowConverters();
	}
	
	private ViewMediator viewMediator() {
		return components.getViewMediator();
	}
}
