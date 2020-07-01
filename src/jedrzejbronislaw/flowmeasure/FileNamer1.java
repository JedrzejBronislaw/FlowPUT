package jedrzejbronislaw.flowmeasure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jedrzejbronislaw.flowmeasure.model.ProcessMetadata;
import jedrzejbronislaw.flowmeasure.model.ProcessRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileNamer1 implements FileNamer {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
	
	@NonNull
	private ProcessRepository process;
	
	
	@Override
	public String createName() {

		ProcessMetadata processMetadata = process.getMetadata();
		
		String processName = processMetadata.getName();
		String authorName  = processMetadata.getAuthor();
		String ifProcessName = processName.isEmpty() ? "" : " " + processName;
		String ifAuthorName  = authorName.isEmpty()  ? "" : " (" +authorName + ")";
		String initFileName  = LocalDateTime.now().format(FORMATTER) + ifProcessName + ifAuthorName;

		return initFileName;
	}

}
