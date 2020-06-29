package jedrzejbronislaw.flowmeasure.model;

import java.time.LocalDateTime;

public class ProcessMetadata {

	private ProcessMetadata_raw metadata = new ProcessMetadata_raw();
	
	public String getAuthor() {return metadata.author;}
	public void setAuthor(String author) {metadata.author = author;}
	
	public String getName() {return metadata.name;}
	public void setName(String name) {metadata.name = name;}
	
	public LocalDateTime getStartTime() {return metadata.startTime;}
	public void setStartTime(LocalDateTime startTime) {metadata.startTime = startTime;}
	
	public LocalDateTime getEndTime() {return metadata.endTime;}
	public void setEndTime(LocalDateTime endTime) {metadata.endTime = endTime;}
}
