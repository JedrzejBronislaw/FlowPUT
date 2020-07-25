package jedrzejbronislaw.flowmeasure.view.chart.components;

import java.util.List;

import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.view.chart.ChartRefresher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public class ChartRange {

	private static final int LAST_SEC_NUMER = 60;
	
	@Getter
	@AllArgsConstructor
	public static class Range{
		private int first;
		private int last;
	}
	
	
	@NonNull private ChartOptions options;
	@NonNull private List<FlowMeasurement> data;
	
	
	public Range get(List<FlowMeasurement> data, ChartOptions options) {
		this.data = data;
		this.options = options;
		
		return new Range(getFirst(), getLast());
	}
	
	private int getFirst() {
		if (!options.isLastSecOption()) return 0;

		int lastIndex = getLast();
		FlowMeasurement lastMeasure = data.get(lastIndex);
		
		for (int i=lastIndex-1; i>=0; i--)
			if (ChartRefresher.timeSec(data.get(i).getTime(), lastMeasure) >= LAST_SEC_NUMER)
				return i;
		
		return 0;
	}

	private int getLast() {
		return data.size()-1;
	}
}
