package jedrzejbronislaw.flowmeasure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jedrzejbronislaw.flowmeasure.model.ProcessMetadata;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileNamer1 implements FileNamer {

	@NonNull
	private ProcessRepository process;
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
	
	
	@Override
	public String createName() {

		ProcessMetadata processMetadata = process.getMetadata();
		String processName = processMetadata.getName();
		String authorName = processMetadata.getAuthor();
		String ifProcessName = processName.isEmpty() ? "" : " " + processName;
		String ifAuthorName = authorName.isEmpty() ? "" : " (" +authorName + ")";
		String initFileName = LocalDateTime.now().format(formatter) + ifProcessName + ifAuthorName;

		return initFileName;
	}

}
