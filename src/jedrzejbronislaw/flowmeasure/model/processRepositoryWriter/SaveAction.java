package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.io.File;

import jedrzejbronislaw.flowmeasure.model.ProcessRepository;

public interface SaveAction {
	public boolean save(ProcessRepository process, File file, ProcessRepositoryWriterOptions options);
}