package jedrzejbronislaw.flowmeasure.devices2.sensors;

import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.devices2.DataFlow;
import jedrzejbronislaw.flowmeasure.devices2.converters.ValueConverter;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public abstract class Sensor {
	
	protected ValueConverter converter;
	protected DataFlow dataFlow;
	
	@Setter private Consumer<Float>  livePreviewOutput;
	@Setter private Consumer<Float>  repositoryOutput;
	@Setter private Consumer<String> calibrationOutput;
	
	public abstract String getTypeName();
//	public abstract String getName();

	public Sensor(ValueConverter converter) {
		this.converter = converter;
	}

	public void setFlow(DataFlow dataFlow) {
		this.dataFlow = dataFlow;
	}
	
	public float receiveData(String data) {
		float value = converter.convert(data);
		
		toLivePreview(value);
		toRepository (value);
		toCalibration(data);
		
		System.out.println(getTypeName() + " received data: " + data + " -> " + value);
		
		return value;
	}

	private void toLivePreview(float value) {
		Injection.run(livePreviewOutput, value);
	}

	private void toRepository(float value) {
		if (dataFlow == DataFlow.REPOSITORY)
			Injection.run(repositoryOutput, value);
	}

	private void toCalibration(String data) {
		if (dataFlow == DataFlow.CALIBRATION)
			Injection.run(calibrationOutput, data);
	}
}
