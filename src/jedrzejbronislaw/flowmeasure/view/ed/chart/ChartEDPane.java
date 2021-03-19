package jedrzejbronislaw.flowmeasure.view.ed.chart;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;
import jedrzejbronislaw.flowmeasure.tools.SnapshotSaver;
import jedrzejbronislaw.flowmeasure.tools.loop.Refresher;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartOptions;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ValueUnit;

public class ChartEDPane extends BorderPane implements Initializable {

	private static final int REFRESHING_TIME = 1000;
	
	@FXML private VBox chartPane;
	@FXML private Button refreshButton, saveButton;
	@FXML private CheckBox lastSecsBox, liveBox;

	private LineChart<Number, Number> chartPH = newChart();
	private LineChart<Number, Number> chartEC = newChart();
	private LineChart<Number, Number> chartAM = newChart();
	
	private ChartEDRefresher chartPHRefresher;
	private ChartEDRefresher chartECRefresher;
	private ChartEDRefresher chartAMRefresher;
	
	private final Supplier<ProcessRepository> currentProcess;
	
	private Refresher liveChartRefresher = new Refresher(REFRESHING_TIME, this::refresh);

	
	public ChartEDPane(FlowConverters flowconverters, Settings settings, Supplier<ProcessRepository> currentProcess) {
		MyFXMLLoader2.create("ChartEDPane.fxml", this);
		
		this.currentProcess = currentProcess;
		
		createChartRefreshers(flowconverters, settings);
	}

	private void createChartRefreshers(FlowConverters flowconverters, Settings settings) {
		chartPHRefresher = new ChartEDRefresher(flowconverters, chartPH, settings);
		chartECRefresher = new ChartEDRefresher(flowconverters, chartEC, settings);
		chartAMRefresher = new ChartEDRefresher(flowconverters, chartAM, settings);
		
		chartPHRefresher.setSeriesFilter(Arrays.asList(0));
		chartECRefresher.setSeriesFilter(Arrays.asList(1));
		chartAMRefresher.setSeriesFilter(Arrays.asList(2));
	}
	
	public LineChart<Number, Number> newChart() {
		return new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		chartPane.getChildren().addAll(chartPH, chartEC, chartAM);
		
		refreshButton.disableProperty().bind(liveBox.selectedProperty());
		
		refreshButton.setOnAction(this::refresh);
		
		saveButton.setOnAction(e -> SnapshotSaver.withFileChooser(chartPane));
		liveBox.setOnAction(e -> {
			if(liveBox.isSelected())
				liveChartRefresher.on(); else
				liveChartRefresher.off();
		});
	}

	private void refresh(ActionEvent x) {refresh();}
	private void refresh() {
		ChartOptions options = getChartOptions();
		
		chartPHRefresher.refresh(options, currentProcess.get());
		chartECRefresher.refresh(options, currentProcess.get());
		chartAMRefresher.refresh(options, currentProcess.get());
	}

	private ChartOptions getChartOptions() {
		return ChartOptions.builder()
				.lastSecOption(isLastSeconds())
				.unit(ValueUnit.PULSES)
				.build();
	}
	
	private boolean isLastSeconds() {
		return lastSecsBox.isSelected();
	}
}
