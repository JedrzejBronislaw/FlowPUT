package jedrzejbronislaw.flowmeasure.builders;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import jedrzejbronislaw.flowmeasure.FlowConverter;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController;
import jedrzejbronislaw.flowmeasure.controllers.ChartPaneController.ValueUnit;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import jedrzejbronislaw.flowmeasure.tools.ItemSelector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChartPaneBuilder extends Builder<ChartPaneController> {

	@Getter private String fxmlFilePath = "ChartPane.fxml";
	
	private final Supplier<ProcessRepository> currentProcess;
	private final FlowConverter flowconverter;
	
	
	private Data<Number, Number> createChartPoint(float time, float value) {
		Data<Number, Number> data = new Data<Number, Number>(time, value);
		
//		flowconverter.pulsesToLitrePerSec(pulses, time, prevTime);
//		data.setNode(new Rectangle(3, 3));
//		data.getNode().setVisible(false);
		
		return data;
	}
	
	@Override
	void afterBuild() {
		controller.setRefreshButtonAction(chart -> {
			Platform.runLater(() -> {
			ProcessRepository repo = currentProcess.get();
			List<FlowMeasurement> data = repo.getAllMeasurement();
			if(data.size() == 0) return;

			LocalDateTime startTime = repo.getMetadata().getStartTime();
			boolean lastSecOption = controller.isLastSeconds();
			ValueUnit unit = controller.getValueUnit();
			NumberAxis axisX = controller.getAxisX();
			NumberAxis axisY = controller.getAxisY();
			
			
			int numOfFlowMeters = repo.getNumOfFlowmeters();
//			int numOfMeasurements = data.size();
			
			List<Series<Number, Number>> seriesList = new LinkedList<>();
			
			//series reuse
			for(int i=0; i<numOfFlowMeters; i++) {
				if(i < chart.getData().size()) {
					Series<Number, Number> s = chart.getData().get(i);
					s.getData().clear();
					seriesList.add(s);
				} else {
					chart.getData().size();
					Series<Number, Number> s = new Series<>();
					s.setName("Flow " + (i+1));//TODO internationalization
					seriesList.add(s);
				}
			}
			
			int first;
			int last;
			if(lastSecOption)
				first = Math.max(0,data.size()-60);
			else {
				first = 0;
				if(data.size() > 1000)
					data = new ItemSelector<FlowMeasurement>().select(data, 1000);
			}
			last = data.size()-1;

			float begin = ChronoUnit.MILLIS.between(startTime, data.get(first).getTime())/1000f;
			float end = ChronoUnit.MILLIS.between(startTime, data.get(last).getTime())/1000f;
			axisX.setLabel("time [s]");
			axisX.setAutoRanging(false);
			axisX.setLowerBound(begin);
			axisX.setUpperBound(end);
//			double axisWidth = axisX.getWidth();
//			axisX.setTickUnit(axisWidth/50);
//			axisX.setMinorTickLength(5);
//			axisX.setMinorTickCount((int)(axisWidth/10));
			axisX.setTickUnit(1);
			axisX.setMinorTickCount(5);

			if(unit == ValueUnit.Pulses){
				FlowMeasurement measurement;
				Data<Number, Number> chartPoint;
				Series<Number, Number> series;
				float time;

				axisY.setLabel("flow [pulses]");
				
				for(int i=first;i<=last;i++){
					measurement = data.get(i);
					time = ChronoUnit.MILLIS.between(startTime, measurement.getTime())/1000f;
					
				
					for(int flowmeter=0; flowmeter<numOfFlowMeters; flowmeter++) {
						chartPoint = createChartPoint(time,  measurement.get(flowmeter));
						series = seriesList.get(flowmeter);
						series.getData().add(chartPoint);	
					}	
				}
			}
			
			//-------------------------
			
			if(unit == ValueUnit.LitrePerSec){
				FlowMeasurement measurement, prevMeasurement;
				Series<Number, Number> series;
				Data<Number, Number> createChartPoint;
				float time;
				float interval;
				int pulses;
				float value;
				
				axisY.setLabel("flow [l/s]");

				for(int i=first;i<=last;i++){
					if(i == 0) continue;
					prevMeasurement = data.get(i-1);
					measurement = data.get(i);
					time = ChronoUnit.MILLIS.between(startTime, measurement.getTime())/1000f;

					for(int flowmeter=0; flowmeter<numOfFlowMeters; flowmeter++) {
						series = seriesList.get(flowmeter);

						interval = ChronoUnit.MILLIS.between(prevMeasurement.getTime(), measurement.getTime())/1000f;
						pulses = measurement.get(flowmeter);
						
						value = flowconverter.pulsesToLitrePerSec(pulses, interval);
						createChartPoint = createChartPoint(time,  value);
						
						series.getData().add(createChartPoint);	
					}
				}

			}
			
			chart.setCreateSymbols(false);
			chart.setAnimated(false);
			
			chart.getData().forEach(s -> {
				if(seriesList.contains(s))
					seriesList.remove(s);
			});
			
			seriesList.forEach(s -> {
				if(!chart.getData().contains(s))
					chart.getData().add(s);	
			});
			});
		});
	}
}
