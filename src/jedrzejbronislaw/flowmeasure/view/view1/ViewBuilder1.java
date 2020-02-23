package jedrzejbronislaw.flowmeasure.view.view1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.ResourcesRepository;
import jedrzejbronislaw.flowmeasure.Session;
import jedrzejbronislaw.flowmeasure.Session.FlowConsumerType;
import jedrzejbronislaw.flowmeasure.Settings;
import jedrzejbronislaw.flowmeasure.UART;
import jedrzejbronislaw.flowmeasure.controllers.CalibrationPaneController;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController.ValueUnit;
import jedrzejbronislaw.flowmeasure.controllers.DialogPaneController;
import jedrzejbronislaw.flowmeasure.controllers.FlowPreviewController;
import jedrzejbronislaw.flowmeasure.controllers.MainWindowController;
import jedrzejbronislaw.flowmeasure.controllers.MeasurementTableController;
import jedrzejbronislaw.flowmeasure.controllers.SettingsPaneController;
import jedrzejbronislaw.flowmeasure.controllers.SidePaneController;
import jedrzejbronislaw.flowmeasure.controllers.UARTParamsController;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.services.Calibration;
import jedrzejbronislaw.flowmeasure.services.EventListener.EventType;
import jedrzejbronislaw.flowmeasure.services.EventManager1;
import jedrzejbronislaw.flowmeasure.tools.ItemSelector;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader.NodeAndController;
import jedrzejbronislaw.flowmeasure.view.ViewBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ViewBuilder1 implements ViewBuilder {
	
	public class ActionContainer{
		@Setter
		private Runnable startButton;
		@Setter
		private Runnable endButton;
		@Setter
		private Runnable saveButton;

		@Setter
		private Runnable connectButton;
		@Setter
		private Runnable disconnectButton;
		@Setter
		private Runnable autoconnectButton;
		
		@Setter
		private Runnable exit;
	}

	@Setter
	private ResourcesRepository resources;

	@Setter
	private EventManager1 eventManager;

	@Setter
	private Calibration calibration;

	
	private static final String FLOW_PREVIEW_FXML = "FlowPreview.fxml";
	private static final String MAIN_WINDOW_FXML = "MainWindow.fxml";
	private static final String SIDE_PANE_FXML = "SidePane.fxml";
	private static final String MEASUREMENT_TABLE_FXML = "MeasurementTable.fxml";
	private static final String CHART_FXML = "ChartPane.fxml";
	private static final String UART_PARAMS_FXML = "UARTParams.fxml";
	private static final String DIALOG_PANE_FXML = "DialogPane.fxml";
	private static final String SETTINGS_PANE_FXML = "SettingsPane.fxml";
	private static final String CALIBRATION_FXML = "CalibrationPane.fxml";
	
	private static final int WINDOW_HEIGHT = 400;
	private static final int WINDOW_WIDTH = 900;

	@NonNull
	private Stage primaryStage;

	@NonNull
	private Session session;
	
	@NonNull
	private Settings settings;
	
	@Setter
	private FlowConverter flowconverter;
	
	@Getter
	private ActionContainer actions = new ActionContainer();
	
	private View1 view;
	private Pane root;
	
//	private FlowDevice getDevice() {
//		return session.getDevice();
//	}
	
	private ProcessRepository getCurrentProcessRepo() {
		return session.getCurrentProcessRepository();
	}
	
	@Override
	public View1 build() {
		view = new View1();
		
		NodeAndController<MainWindowController> mainWindowNAC = mainWindow();
		
		root = (Pane) mainWindowNAC.getNode();
		buildWindow(root);
		buildDialog();
		
		return view;
	}
	
	private void buildDialog() {
				
		view.showDialog = (title, content, closeDelay) -> {
			MyFXMLLoader<DialogPaneController> loader = new MyFXMLLoader<>();
			NodeAndController<DialogPaneController> nac = loader.create(DIALOG_PANE_FXML);
			DialogPaneController controller = nac.getController();
			
			((Pane)nac.getNode()).setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,CornerRadii.EMPTY, Insets.EMPTY)));
			controller.setNode(nac.getNode());
			controller.setMessage(title, content);
			Platform.runLater(() -> 
				root.getChildren().add(nac.getNode())
				);
			controller.close(closeDelay);
		};
		
	}
	
	private void buildWindow(Pane root) {
		try {
			Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
//			scene.getStylesheets().add(getClass().getResource("/jedrzejbronislaw/flowmeasure/application/"+"application.css").toExternalForm());
//			scene.getStylesheets().add(new URL("file:D:/temp/"+"application.css").toExternalForm());
			scene.getStylesheets().add(resources.getResourcePath("application.css"));
			primaryStage.setScene(scene);
			primaryStage.setTitle("FlowmeterPP");
			primaryStage.setOnCloseRequest(e -> {
				if(actions.exit != null)
					actions.exit.run();
//				getDevice().disconnect();
				Platform.exit();
			});
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private NodeAndController<UARTParamsController> uart() {
		MyFXMLLoader<UARTParamsController> loader = new MyFXMLLoader<>();
		NodeAndController<UARTParamsController> nac = loader.create(UART_PARAMS_FXML);
		UARTParamsController controller = nac.getController();
		
		controller.setPortsNames(UART.getPortList());
		controller.setRefreshPortsButtonAction(() -> controller.setPortsNames(UART.getPortList()));
//		uartNAC.getController().setRates(UART.getRateList());
//		uartNAC.getController().setRate("9600");

		controller.setConnectButtonAction(() -> {
			if(actions.connectButton != null)
				actions.connectButton.run();
		});
		controller.setDisconnectButtonAction(() -> {
			if(actions.disconnectButton != null)
				actions.disconnectButton.run();
		});
		controller.setAutoConnectButtonAction(() -> {
			if(actions.autoconnectButton != null)
				actions.autoconnectButton.run();		
		});
		
		view.getUARTParams = () -> controller.getParams();
		
//		view.connecting = () -> {
//			uartNAC.getController().setDisableFields(true);
//		};
//		view.connected = () -> {
//			uartNAC.getController().setDisableFields(true);
//			System.out.println("PO£ACZONO");
////			view.showDialog("Po³¹czono", "Nawi¹zano po³¹czenie z urz¹dzeniem FlowPP", 2000);
//		};
//		view.disconnected = () -> {
//			uartNAC.getController().setDisableFields(false);
//			System.out.println("NIE PO£ACZONO");
//		};
		
		session.getConnState().addStateListiner(controller);
		
		return nac;
	}
	
	
	
	private Data<Number, Number> createChartData(float time, float value) {
		Data<Number, Number> data = new Data<Number, Number>(time, value);
		
//		flowconverter.pulsesToLitrePerSec(pulses, time, prevTime);
//		data.setNode(new Rectangle(3, 3));
//		data.getNode().setVisible(false);
		
		return data;
	}
	
	private NodeAndController<ChartPaneController> chart() {
		MyFXMLLoader<ChartPaneController> loader = new MyFXMLLoader<>();
		NodeAndController<ChartPaneController> nac = loader.create(CHART_FXML);
		
		nac.getController().setRefreshButtonAction(chart -> {
			System.out.println("max hi" + chart.getMaxHeight());
			System.out.println("max wi" + chart.getMaxWidth());
			ProcessRepository repo = getCurrentProcessRepo();
			List<FlowMeasurement> data = repo.getAllMeasurementCopy();
//			List<FlowMeasurement> data2 = session.getSecondProcessRepository().getAllMeasurementCopy();
			if(data.size() == 0) return;
			LocalDateTime startTime = repo.getMetadata().getStartTime();
			boolean lastSecOption = nac.getController().isLastSeconds();
			ValueUnit unit = nac.getController().getValueUnit();
			nac.getController().getAxisX().setLabel("time [s]");
			
			int numOfFlowMeters = repo.getNumOfFlowmeters();
//			int numOfMeasurements = data.size();
			
			List<Series<Number, Number>> series = new LinkedList<>();
			

//			chart.getData().forEach(legacySeries -> series.add(legacySeries));
			
			for(int i=0; i<numOfFlowMeters; i++) {
				if(i < chart.getData().size()) {
					Series<Number, Number> s = chart.getData().get(i);
					s.getData().clear();
					series.add(s);
				} else {
					chart.getData().size();
					Series<Number, Number> s = new Series<>();
					s.setName("Flow " + (i+1));//TODO internationalization
					series.add(s);
				}
			}
			
			int first;
			if(lastSecOption)
				first = Math.max(0,data.size()-60);
			else {
				first = 0;
				data = new ItemSelector<FlowMeasurement>().select(data, 1000);
			}
			int last = data.size()-1;

			float begin = ChronoUnit.MILLIS.between(startTime, data.get(first).getTime())/1000f;
			float end = ChronoUnit.MILLIS.between(startTime, data.get(last).getTime())/1000f;
			nac.getController().getAxisX().setAutoRanging(false);
			nac.getController().getAxisX().setLowerBound(begin);
			nac.getController().getAxisX().setUpperBound(end);
			nac.getController().getAxisX().setTickUnit(1);
//			nac.getController().getAxisX().setMinorTickLength(5);
			nac.getController().getAxisX().setMinorTickCount(5);

			if(unit == ValueUnit.Pulses){
				nac.getController().getAxisY().setLabel("flow [pulses]");
			FlowMeasurement measurement;
			for(int i=first;i<=last;i++){
				measurement = data.get(i);
//			data.forEach(measurement -> {
				float time = ChronoUnit.MILLIS.between(startTime, measurement.getTime())/1000f;
					
				
				for(int flowmeter=0; flowmeter<numOfFlowMeters; flowmeter++) {
//					if(flowmeter != 1)
					series.get(flowmeter).getData().add(createChartData(time,  measurement.get(flowmeter)));	
				}	
			}
//			for(int i=first;i<=data2.size()-1;i++){
//	
//					series.get(1).getData().add(createChartData(ChronoUnit.MILLIS.between(session.getSecondProcessRepository().getMetadata().getStartTime(), data2.get(i).getTime())/1000f,  data2.get(i).get(0)));
//			}
			}
			
			//-------------------------
			
			if(unit == ValueUnit.LitrePerSec){
				nac.getController().getAxisY().setLabel("flow [l/s]");
				FlowMeasurement measurement, prevMeasurement;
				for(int i=first;i<=last;i++){
					if(i == 0) continue;
					prevMeasurement = data.get(i-1);
					measurement = data.get(i);
//			data.forEach(measurement -> {
					float time = ChronoUnit.MILLIS.between(startTime, measurement.getTime())/1000f;
					float interval;
				
					for(int flowmeter=0; flowmeter<numOfFlowMeters; flowmeter++) {
//						if(flowmeter == 1)continue;
						interval = ChronoUnit.MILLIS.between(prevMeasurement.getTime(), measurement.getTime())/1000f;
						
						int pulses = measurement.get(flowmeter);
						
						float value = flowconverter.pulsesToLitrePerSec(pulses, interval);
						series.get(flowmeter).getData().add(createChartData(time,  value));	
					}
				}
//				for(int i=first;i<=data2.size()-1;i++){
//					if(i == 0) continue;
//					prevMeasurement = data2.get(i-1);
//					measurement = data2.get(i);
//					float interval = ChronoUnit.MILLIS.between(prevMeasurement.getTime(), measurement.getTime())/1000f;
//					
//					int pulses = data2.get(i).get(0);
//					float value = flowconverter.pulsesToLitrePerSec(pulses, interval);
//					series.get(1).getData().add(createChartData(ChronoUnit.MILLIS.between(session.getSecondProcessRepository().getMetadata().getStartTime(), measurement.getTime())/1000f,  value));
//				}
			}
//			);
			
			chart.setCreateSymbols(false);
			chart.setAnimated(false);
			
//			chart.getData().clear();
//			chart.getData().addAll(series);
			chart.getData().forEach(s -> {
				if(series.contains(s))
					series.remove(s);
			});
			
			series.forEach(s -> {
				if(!chart.getData().contains(s))
					chart.getData().add(s);	
			});
		});
		
//		nac.getController().setLiveBoxAction(() -> {
//			
//		});
		
		return nac;
	}
	

	private NodeAndController<MeasurementTableController> table() {
		MyFXMLLoader<MeasurementTableController> loaderMeasurementTable = new MyFXMLLoader<>();
		NodeAndController<MeasurementTableController> measurementTableNAC = loaderMeasurementTable.create(MEASUREMENT_TABLE_FXML);
		
		measurementTableNAC.getController().setRefreshButtonAction(table -> {
			int size = getCurrentProcessRepo().getNumOfFlowmeters();

			table.getItems().clear();
			table.getColumns().clear();
			
			TableColumn<FlowMeasurement, String> columnT;
			columnT = new TableColumn<>();
//			DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		    
			columnT.setText("Time");
			columnT.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FlowMeasurement,String>, ObservableValue<String>>() {
				
				@Override
				public ObservableValue<String> call(CellDataFeatures<FlowMeasurement, String> fm) {
					return new SimpleStringProperty(fm.getValue().getTime().format(timeFormat));
				}
			});
			
			table.getColumns().add(columnT);
			
			TableColumn<FlowMeasurement, Integer> column;
			for(int i=0; i<size; i++) {
				column = new TableColumn<>();
		        
				column.setText("Flow " + (i+1));
				int ii = i;
				column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FlowMeasurement,Integer>, ObservableValue<Integer>>() {
					
					@Override
					public ObservableValue<Integer> call(CellDataFeatures<FlowMeasurement, Integer> fm) {

						return new SimpleIntegerProperty(fm.getValue().get(ii)).asObject();
					}
				});
				
				table.getColumns().add(column);
			}
			
			table.getItems().addAll(getCurrentProcessRepo().getAllMeasurementCopy());
			table.scrollTo(table.getItems().size()-1);
		});
		
		return measurementTableNAC;
	}
	
	private NodeAndController<SidePaneController> sidePane(){
		MyFXMLLoader<SidePaneController> loader = new MyFXMLLoader<>();
		NodeAndController<SidePaneController> nac = loader.create(SIDE_PANE_FXML);
		SidePaneController controller = nac.getController();

		controller.setStartButtonAction(() -> {
			if(actions.startButton != null)
				actions.startButton.run();
//			session.getCurrentProcessRepository().setProcessStartTimeNow();
//			session.setProcessState(ProcessState.Ongoing);
		});
		controller.setEndButtonAction(() -> {
			if(actions.endButton != null)
				actions.endButton.run();
//			session.getCurrentProcessRepository().setProcessEndTimeNow();
//			session.setProcessState(ProcessState.Finished);
		});
		controller.setSaveButtonAction(() -> {
			if(actions.saveButton != null)
				actions.saveButton.run();
//			ProcessRepositoryWriter writer = new ProcessRepositoryCSVWriter();
//			
//			writer.save(session.getCurrentProcessRepository(), new File("D:\\fm.txt"));
		});
		
//		view.diodeBlink = () -> controller.diodeBlink();
//		session.addProcessStateListiner((state) -> controller.getProcessStateLabel().setText(state.toString()));
//		session.getProcessState().addStateListiner((state) -> controller.getProcessStateLabel().setText(state.toString()));
		session.getProcessState().addStateListiner(controller);
		
		eventManager.addListener(controller);
		
		return nac;
	}
	
	private NodeAndController<SettingsPaneController> settingsPane(){
		MyFXMLLoader<SettingsPaneController> loader = new MyFXMLLoader<SettingsPaneController>();
		NodeAndController<SettingsPaneController> nac = loader.create(SETTINGS_PANE_FXML);

		SettingsPaneController controller = nac.getController();
		
		controller.setSettings(settings);
		
		controller.setSavingAction(() -> {
			try {
				settings.setPulsePerLitre(controller.getPulsesPerLitre());
			} catch (NumberFormatException e) {
				
			}
			settings.setBufferedData(controller.isSelectedBuffer());
			try {
				settings.setBufferInterval(controller.getBufferSize());
			} catch (NumberFormatException e) {
				
			}
			
			settings.write();
		});
		
		settings.addChangeListiner(() -> controller.setSettings(settings));
		
		return nac;
	}

	private NodeAndController<CalibrationPaneController> calibration() {
		MyFXMLLoader<CalibrationPaneController> loader = new MyFXMLLoader<>();
		NodeAndController<CalibrationPaneController> nac = loader.create(CALIBRATION_FXML);
		CalibrationPaneController controller = nac.getController();
		
		controller.setStart(() -> {
//			if(session.getAppState().getState() == ApplicationState.Idle) {
			if(eventManager.event(EventType.Calibration_Starts)) {
//				eventManager.event(EventType.Calibration_Starts);
//				session.getAppState().setState(ApplicationState.Calibration);
				session.setFlowConsumerType(FlowConsumerType.Calibration);
			}
		});
		controller.setStop(() -> {
//			if(session.getAppState().getState() == ApplicationState.Calibration) {
			if(eventManager.event(EventType.Calibration_Ends)) {
//				eventManager.event(EventType.Calibration_Ends);	
//				session.getAppState().setState(ApplicationState.Idle);
				session.setFlowConsumerType(FlowConsumerType.None);
			}
		});
		controller.setReset(() -> {
			calibration.reset();
		});
		controller.setSet(() -> {
			settings.setPulsePerLitre(calibration.getValue());
			settings.write();
		});
		
		calibration.setValueListener(value -> controller.setCurrentValue(value));
		
		eventManager.addListener(controller);
		
		return nac;
	}
	
	private NodeAndController<MainWindowController> mainWindow() {
		MyFXMLLoader<MainWindowController> loaderMainWindow = new MyFXMLLoader<>();
		NodeAndController<MainWindowController> mainWindowNAC = loaderMainWindow.create(MAIN_WINDOW_FXML);
		
		MyFXMLLoader<FlowPreviewController> loaderFlowPreview = new MyFXMLLoader<>();
		List<NodeAndController<FlowPreviewController>> flowPreviewNAC = new ArrayList<>();
		NodeAndController<FlowPreviewController> nac;

//		FlowConverter flowconverter = new FlowConverter1(settings);
		
		for(int i=0; i< session.getCurrentProcessRepository().getNumOfFlowmeters(); i++) {
			nac = loaderFlowPreview.create(FLOW_PREVIEW_FXML);
			flowPreviewNAC.add(nac);
			nac.getController().setNumber(i);
			nac.getController().setFlowconverter(flowconverter);
			mainWindowNAC.getController().addFlowPreview(nac.getNode());
			view.flowViews.put(i, nac);
		}
		
		NodeAndController<UARTParamsController> uartNAC = uart();
		NodeAndController<MeasurementTableController> measurementTableNAC = table();
		NodeAndController<ChartPaneController> chartPaneNAC = chart();
		NodeAndController<CalibrationPaneController> calibrationPaneNAC = calibration();
		NodeAndController<SidePaneController> sidePaneNAC = sidePane();
		NodeAndController<SettingsPaneController> settingsPaneNAC = settingsPane();

		mainWindowNAC.getController().getBorderPane().setLeft(sidePaneNAC.getNode());
		mainWindowNAC.getController().getBorderPane().setRight(uartNAC.getNode());
		mainWindowNAC.getController().setTablePane(measurementTableNAC.getNode());
		mainWindowNAC.getController().setChartPane(chartPaneNAC.getNode());
		mainWindowNAC.getController().setSettingsPane(settingsPaneNAC.getNode());
		mainWindowNAC.getController().setCalibrationPane(calibrationPaneNAC.getNode());
		
		return mainWindowNAC;
	}
	
}
