package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class ProcessRepositoryWriterOptions {

	public enum TimeFormat{
		Unix, Full, ProcessTime
	}
	
	public enum Columns{
		Pulses, Flow
	}
	
	@Getter
	private Set<TimeFormat> timeFormats = new HashSet<>();
	@Getter
	private List<Columns> columns = new ArrayList<>();
	@Getter
	@Setter
	private boolean flowmeterValuesTogether;
	@Getter
	@Setter
	private boolean commaSeparator;

}
