package jedrzejbronislaw.flowmeasure.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcessMetadata {

	private String author;
	private String name;
	
	private LocalDateTime startTime;
	private LocalDateTime endTime;
}
