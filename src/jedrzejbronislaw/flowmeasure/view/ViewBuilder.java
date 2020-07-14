package jedrzejbronislaw.flowmeasure.view;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.builders.CalibrationPaneBuild;
import jedrzejbronislaw.flowmeasure.builders.ChartPaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.DialogPaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.FlowPreviewBuilder;
import jedrzejbronislaw.flowmeasure.builders.MainWindowBuilder;
import jedrzejbronislaw.flowmeasure.builders.MeasurementTableBuilder;
import jedrzejbronislaw.flowmeasure.builders.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.SidePaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.UARTParamsBuilder;
import jedrzejbronislaw.flowmeasure.events.EventManager;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.DialogManager;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ViewBuilder {
	
	private static final int WINDOW_HEIGHT = 400;
	private static final int WINDOW_WIDTH = 900;
	private static final String WINDOW_TITLE = "FlowmeterPP";
	private static final String CSS_FILENAME = "application.css";

	@NonNull
	private Components components;
	
	@NonNull
	private ActionContainer actions;
	
	
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
			
			primaryStage().setScene(scene);
			primaryStage().setTitle(WINDOW_TITLE);
			primaryStage().setOnCloseRequest(e -> {
				actions.exit();
				Platform.exit();
			});
			primaryStage().show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Node uart() {
		UARTParamsBuilder builder = new UARTParamsBuilder(actions);
		builder.build();
		
		viewMediator().setUartParamsGetter(builder.getController()::getParams);
		stateManager().getConnState().addStateListiner(builder.getController());
		
		return builder.getNode();
	}
	
	
	private Node chart() {
		ChartPaneBuilder builder = new ChartPaneBuilder(this::getCurrentProcessRepo, flowconverter());
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
		
		stateManager().getProcessState().addStateListiner(builder.getController());
		eventManager().addListener(builder.getController());
		
		return builder.getNode();
	}
	
	private Node settingsPane(){
		SettingsPaneBuilder builder = new SettingsPaneBuilder(settings());
		builder.build();
		
		stateManager().getProcessState().addStateListiner(builder.getController());
		
		return builder.getNode();
	}

	private Node calibrationPane() {
		CalibrationPaneBuild builder = new CalibrationPaneBuild(eventManager(), session(), settings(), calibration());
		builder.build();
		
		return builder.getNode();
	}

	private Node flowPreview(int number) {
		FlowPreviewBuilder builder = new FlowPreviewBuilder(number, flowconverter());
		builder.build();
		
		viewMediator().setFlowPreviewer(number, builder.getController()::addPulses);
		
		return builder.getNode();
	}
	
	private Node mainWindow() {
		MainWindowBuilder builder = new MainWindowBuilder();
		builder.build();
		
		for(int i=0; i<6; i++)
			builder.getController().addFlowPreview(flowPreview(i));

		builder.getController().getBorderPane().setLeft(sidePane());
		builder.getController().getBorderPane().setRight(uart());
		builder.getController().setTablePane(table());
		builder.getController().setChartPane(chart());
		builder.getController().setSettingsPane(settingsPane());
		builder.getController().setCalibrationPane(calibrationPane());

		return builder.getNode();
	}
	
	
	private ProcessRepository getCurrentProcessRepo() {
		return session().getCurrentProcessRepository();
	}

	
	private Stage primaryStage() {
		return components.getPrimaryStage();
	}
	
	private Session session() {
		return components.getSession();
	}
	
	private Settings settings() {
		return components.getSettings();
	}

	
	private ResourcesRepository resources() {
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
	
	private FlowConverter flowconverter() {
		return components.getFlowConverter();
	}
	
	private ViewMediator viewMediator() {
		return components.getViewMediator();
	}
}
