package jedrzejbronislaw.flowmeasure.components.calibration;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Getter;
import lombok.Setter;

public class Calibration1 implements Calibration {

	@Setter private int flowmeter;
	@Getter private float aveValue = 0;
	        private List<Integer> values = new LinkedList<>();
	@Setter private float volume = 0;

	@Setter private Consumer<Float>         aveValueListener = null;
	@Setter private Consumer<List<Integer>> valuesListener   = null;


	public Calibration1() {
		newMeasure();
	}
	
	@Override
	public void addFlowMeasurement(int[] pulses) {
		addToCurrentMeasure(selectFlow(pulses));
		computeAverage();
		listenerAction();
	}

	private int selectFlow(int[] pulses) {
		try {
			return pulses[flowmeter-1];
		} catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	@Override
	public void reset() {
		values.clear();
		newMeasure();
		computeAverage();
		listenerAction();
	}

	private void addToCurrentMeasure(int pulse) {
		int index  = values.size()-1;
		int oldVal = values.get(index);
		
		values.set(index, oldVal + pulse);
	}

	private void computeAverage() {
		aveValue = (float) values.stream().mapToInt(x->x).average().orElse(0);
	}

	@Override
	public void newMeasure() {
		values.add(0);
	}
	
	private void listenerAction() {
		Injection.run(aveValueListener, aveValue);
		Injection.run(valuesListener,   Collections.unmodifiableList(values));
	}
}
