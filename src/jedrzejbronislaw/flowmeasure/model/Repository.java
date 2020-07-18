package jedrzejbronislaw.flowmeasure.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Repository {
	
	private final static int FLOWMETERS_NUMBER = 6;
	
	@Getter
	private ProcessRepository currentProcessRepository;
	private List<ProcessRepository> processRepositories = new ArrayList<>();
	
	
	public ProcessRepository createNewProcessRepository(String name) {
		ProcessRepository processRepository = new ProcessRepository(FLOWMETERS_NUMBER, name);
		processRepository.getMetadata().setAuthor("unknown");
		
		processRepositories.add(processRepository);
		currentProcessRepository = processRepository;
		
		return processRepository;
	}
}
