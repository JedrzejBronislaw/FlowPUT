package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Parser {
	
	private final String content;

	protected static List<String[]> metadata;
	
	public Parser(String content) {
		this.content = content;
		parse();
	}
	
	private void parse() {
		String[] lines = content.split(Pattern.quote(ProcessRepositoryCSVWriter.NEW_LINE_SEPARATOR));
		metadata = extractMetadata(lines);
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
