package jedrzejbronislaw.flowmeasure.model;

import java.time.LocalDateTime;

public class ProcessMetadata {

	private ProcessMetadata_raw metadate = new ProcessMetadata_raw();
	
	public String getAuthor() {return metadate.author;}
	public void setAuthor(String author) {metadate.author = author;}
	
	public String getName() {return metadate.name;}
	public void setName(String name) {metadate.name = name;}
	
	public LocalDateTime getStartTime() {return metadate.startTime;}
	public void setStartTime(LocalDateTime startTime) {metadate.startTime = startTime;}
	
	public LocalDateTime getEndTime() {return metadate.endTime;}
	public void setEndTime(LocalDateTime endTime) {metadate.endTime = endTime;}
}
