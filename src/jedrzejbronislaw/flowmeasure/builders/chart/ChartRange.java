package jedrzejbronislaw.flowmeasure.builders.chart;

import java.util.List;

import jedrzejbronislaw.flowmeasure.builders.ChartRefresher;
import jedrzejbronislaw.flowmeasure.model.FlowMeasurement;
import jedrzejbronislaw.flowmeasure.tools.ItemSelector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public class ChartRange {

	private static final int LAST_SEC_NUMER = 60;
	private static final int DATA_SIZE_LIMIT = 1000;
	
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
		
		reduceDataIfNecessary();
		
		return new Range(getFirst(), getLast());
	}
	

	private void reduceDataIfNecessary() {
		if(!options.isLastSecOption() && data.size() > DATA_SIZE_LIMIT)
			data = new ItemSelector<FlowMeasurement>().select(data, DATA_SIZE_LIMIT);
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
