package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.io.StringWriter;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringFile {
	
	private final ProcessRepository repository;
	private final ProcessRepositoryWriterOptions options;
	
	private ProcessRepositoryCSVWriter processWriter;
	private StringWriter writer = new StringWriter();
	
	public String createContentOfFile() {
		processWriter = new ProcessRepositoryCSVWriter();
		processWriter.setWriterCreator(file -> writer);
		
		return save(repository, options);
	}
	
	private String save(ProcessRepository repository, ProcessRepositoryWriterOptions options) {
		processWriter.save(repository, null, options);
		return writer.toString();
	}
}
