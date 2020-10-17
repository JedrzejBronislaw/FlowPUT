package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowUnit;
import lombok.Getter;
import lombok.Setter;

public class ProcessRepositoryWriterOptions {

	public enum TimeFormat{UNIX, FULL, PROCESS_TIME}
	public enum Unit{PULSES, FLOW}
	public enum DecimalSeparator{POINT("."), COMMA(",");

		private String separator;
		
		DecimalSeparator(String separator) {
			this.separator = separator;
		}
		
		@Override
		public String toString() {
			return separator;
		}
	}
	
	@Getter         private Set<TimeFormat> timeFormats = new HashSet<>();
	@Getter         private List<Unit> units = new ArrayList<>();
	@Getter @Setter private boolean flowmeterValuesTogether;
	@Getter @Setter private DecimalSeparator decimalSeparator = DecimalSeparator.POINT;
	@Getter @Setter private FlowUnit flowUnit = FlowUnit.LITER_PER_HOUR;

	@Getter @Setter private boolean saveMetadata = true;
	@Getter @Setter private boolean saveHeaders  = true;
}
