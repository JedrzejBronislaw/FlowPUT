package jedrzejbronislaw.flowmeasure.model;

import java.util.ArrayList;
import java.util.List;

public class Repository {
	
	private List<ProcessRepository> processRepositories = new ArrayList<>();
	
	public ProcessRepository createNewProcessRepository(int size, String name) {
		ProcessRepository repository = new ProcessRepository(size, name);
		
		processRepositories.add(repository);
		
		return repository;
	}
}
