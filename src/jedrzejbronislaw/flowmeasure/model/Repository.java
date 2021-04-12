package jedrzejbronislaw.flowmeasure.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Repository {
	
	@Getter
	private ProcessRepository currentProcessRepository;
	private List<ProcessRepository> processRepositories = new ArrayList<>();
	
	
	public ProcessRepository createNewProcessRepository(int numberOfSensors, String name) {
		ProcessRepository processRepository = new ProcessRepository(numberOfSensors, name);
		processRepository.getMetadata().setAuthor("");
		
		processRepositories.add(processRepository);
		currentProcessRepository = processRepository;
		
		return processRepository;
	}
	
	public void closeCurrentProcessRepository() {
		currentProcessRepository = null;
	}
}
