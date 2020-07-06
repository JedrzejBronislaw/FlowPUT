package jedrzejbronislaw.flowmeasure.view.view1;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.builders.CalibrationPaneBuild;
import jedrzejbronislaw.flowmeasure.builders.ChartPaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.DialogPaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.FlowPreviewBuilder;
import jedrzejbronislaw.flowmeasure.builders.MainWindowBuilder;
import jedrzejbronislaw.flowmeasure.builders.MeasurementTableBuilder;
import jedrzejbronislaw.flowmeasure.builders.SettingsPaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.SidePaneBuilder;
import jedrzejbronislaw.flowmeasure.builders.UARTParamsBuilder;
import jedrzejbronislaw.flowmeasure.events.EventManager1;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.DialogManager1;
import jedrzejbronislaw.flowmeasure.states.StateManager;
import jedrzejbronislaw.flowmeasure.view.ActionContainer;
import jedrzejbronislaw.flowmeasure.view.ViewBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ViewBuilder1 implements ViewBuilder {
	
	private static final int WINDOW_HEIGHT = 400;
	private static final int WINDOW_WIDTH = 900;
	private static final String WINDOW_TITLE = "FlowmeterPP";
	private static final String CSS_FILENAME = "application.css";
	
	@NonNull
	private Stage primaryStage;
	
	@NonNull
	private Session session;
	
	@NonNull
	private Settings settings;

	
	@Setter
	private ResourcesRepository resources;

	@Setter
	private EventManager1 eventManager;

	@Setter
	private StateManager stateManager;

	@Setter
	private DialogManager1 dialogManager;
	
	@Setter
	private Calibration calibration;

	@Setter
	private FlowConverter flowconverter;
	
	@Setter
	private ActionContainer actions;
	
	@Setter
	private ViewMediator1 viewMediator;
	
	
	private Pane root;
	
	
	@Override
	public void build() {
		root = (Pane) mainWindow();
		buildWindow(root);
		buildDialog();
	}
	
	private void buildDialog() {
		DialogPaneBuilder builder = new DialogPaneBuilder(root);
		builder.build();
		
		dialogManager.setShowMessage(builder.getController()::show);
	}
	
	private void buildWindow(Pane root) {
		try {
			
			Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
			scene.getStylesheets().add(resources.getResourcePath(CSS_FILENAME));
			
			primaryStage.setScene(scene);
			primaryStage.setTitle(WINDOW_TITLE);
			primaryStage.setOnCloseRequest(e -> {
				actions.exit();
				Platform.exit();
			});
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Node uart() {
		UARTParamsBuilder builder = new UARTParamsBuilder(actions);
		builder.build();
		
		viewMediator.setUartParamsGetter(builder.getController()::getParams);
		stateManager.getConnState().addStateListiner(builder.getController());
		
		return builder.getNode();
	}
	
	
	private Node chart() {
		ChartPaneBuilder builder = new ChartPaneBuilder(this::getCurrentProcessRepo, flowconverter);
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
		
		stateManager.getProcessState().addStateListiner(builder.getController());
		eventManager.addListener(builder.getController());
		
		return builder.getNode();
	}
	
	private Node settingsPane(){
		SettingsPaneBuilder builder = new SettingsPaneBuilder(settings);
		builder.build();
		
		return builder.getNode();
	}

	private Node calibration() {
		CalibrationPaneBuild builder = new CalibrationPaneBuild(eventManager, session, settings, calibration);
		builder.build();
		
		return builder.getNode();
	}

	private Node flowPreview(int number) {
		FlowPreviewBuilder builder = new FlowPreviewBuilder(number, flowconverter);
		builder.build();
		
		viewMediator.setFlowPreviewer(number, builder.getController()::addPulses);
		
		return builder.getNode();
	}
	
	private Node mainWindow() {
		MainWindowBuilder builder = new MainWindowBuilder();
		builder.build();
		
		for(int i=0; i< getCurrentProcessRepo().getNumOfFlowmeters(); i++)
			builder.getController().addFlowPreview(flowPreview(i));

		builder.getController().getBorderPane().setLeft(sidePane());
		builder.getController().getBorderPane().setRight(uart());
		builder.getController().setTablePane(table());
		builder.getController().setChartPane(chart());
		builder.getController().setSettingsPane(settingsPane());
		builder.getController().setCalibrationPane(calibration());

		return builder.getNode();
	}
	
	private ProcessRepository getCurrentProcessRepo() {
		return session.getCurrentProcessRepository();
	}
}
