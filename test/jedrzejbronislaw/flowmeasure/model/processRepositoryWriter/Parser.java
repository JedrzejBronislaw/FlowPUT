package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Parser {
	
	private final String content;

	private List<String[]> metadata;
	private List<String[]> dataHeader;
	private List<String[]> data;
	
	public List<String[]> getMetadata() {
		return Collections.unmodifiableList(metadata);
	}
	public List<String[]> getDataHeader() {
		return Collections.unmodifiableList(dataHeader);
	}
	public List<String[]> getData() {
		return Collections.unmodifiableList(data);
	}
	
	public Parser(String content) {
		this.content = content;
		parse();
	}
	
	private void parse() {
		String[] lines = content.split(Pattern.quote(ProcessRepositoryCSVWriter.NEW_LINE_SEPARATOR));
		metadata = extractMetadata(lines);
		dataHeader = extractDataHeader(lines);
		data = extractData(lines);
	}
	
	private static List<String[]> extractMetadata(String[] lines) {
		List<String[]> metadata = new ArrayList<>();
		boolean metadataLine = false;
		
		for(String line : lines) {
			
			if(line.equals(ProcessRepositoryCSVWriter.METADATA_HEAD)) metadataLine = true;  else
			if(line.equals(ProcessRepositoryCSVWriter.DATA_HEAD))     metadataLine = false; else
			
			if (metadataLine && !line.trim().isEmpty())
				metadata.add(line.split(ProcessRepositoryCSVWriter.SEPARATOR));
		}
		
		return metadata;
	}
	
	private static List<String[]> extractDataHeader(String[] lines) {
		List<String[]> dataheader = new ArrayList<>();
		
		for(int i=0; i<lines.length-2; i++) {
			String line = lines[i];
			
			if(line.equals(ProcessRepositoryCSVWriter.DATA_HEAD)) {
				dataheader.add(lines[i+1].split(ProcessRepositoryCSVWriter.SEPARATOR));
				dataheader.add(lines[i+2].split(ProcessRepositoryCSVWriter.SEPARATOR));
				break;
			}
		}
		
		return dataheader;
	}
	
	private static List<String[]> extractData(String[] lines) {
		List<String[]> data = new ArrayList<>();
		boolean dataLine = false;
		
		for(int i=0; i<lines.length; i++) {
			String line = lines[i];
			
			if(line.equals(ProcessRepositoryCSVWriter.DATA_HEAD)) {
				dataLine = true;
				i += 2;
			} else
			
			if (dataLine)
				data.add(line.split(ProcessRepositoryCSVWriter.SEPARATOR));
		}
		
		return data;
	}
	
	public boolean propertyExists(String propretyName) {
		for (String[] property : metadata)
			if (property[0].equals(propretyName)) return true;
		
		return false;
	}
	
	public String[] getProperty(String propretyName) {
		for (String[] property : metadata)
			if (property[0].equals(propretyName)) return property;
		
		return null;
	}
}
