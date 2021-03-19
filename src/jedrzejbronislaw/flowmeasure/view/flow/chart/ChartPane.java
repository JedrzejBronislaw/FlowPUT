package jedrzejbronislaw.flowmeasure.view.flow.chart;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;
import jedrzejbronislaw.flowmeasure.tools.SnapshotSaver;
import jedrzejbronislaw.flowmeasure.tools.loop.Refresher;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ChartOptions;
import jedrzejbronislaw.flowmeasure.view.flow.chart.components.ValueUnit;
import lombok.Getter;

public class ChartPane extends BorderPane implements Initializable {

	private static final int REFRESHING_TIME = 1000;
	
	@FXML private Button refreshButton, saveButton;
	@FXML private CheckBox lastSecsBox, liveBox;
	@FXML private RadioButton pulsesRadio, litresPerSecRadio;
	
	@Getter private LineChart<Number, Number> chart = newChart();

	private ChartRefresher chartRefresher;
	
	private final Supplier<ProcessRepository> currentProcess;
	
	private Refresher liveChartRefresher = new Refresher(REFRESHING_TIME, this::refresh);

	
	public ChartPane(FlowConverters flowconverters, Settings settings, Supplier<ProcessRepository> currentProcess) {
		MyFXMLLoader2.create("ChartPane.fxml", this);
		
		this.currentProcess = currentProcess;

		chartRefresher = new ChartRefresher(flowconverters, chart, settings);
	}
	
	public LineChart<Number, Number> newChart() {
		return new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setCenter(chart);
		
		refreshButton.disableProperty().bind(liveBox.selectedProperty());
		
		refreshButton    .setOnAction(this::refresh);
		pulsesRadio      .setOnAction(this::refresh);
		litresPerSecRadio.setOnAction(this::refresh);
		
		saveButton.setOnAction(e -> SnapshotSaver.withFileChooser(chart));
		liveBox.setOnAction(e -> {
			if(liveBox.isSelected())
				liveChartRefresher.on(); else
				liveChartRefresher.off();
		});
	}

	private void refresh(ActionEvent x) {refresh();}
	private void refresh() {
		chartRefresher.refresh(getChartOptions(), currentProcess.get());
	}

	private ChartOptions getChartOptions() {
		return ChartOptions.builder()
				.lastSecOption(isLastSeconds())
				.unit(getValueUnit())
				.build();
	}
	
	private boolean isLastSeconds() {
		return lastSecsBox.isSelected();
	}

	private ValueUnit getValueUnit(){
		if(pulsesRadio.isSelected())       return ValueUnit.PULSES;
		if(litresPerSecRadio.isSelected()) return ValueUnit.FLOW_UNIT;
		return null;
	}
}
