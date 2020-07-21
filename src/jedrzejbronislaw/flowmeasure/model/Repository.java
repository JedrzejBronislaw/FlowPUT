package jedrzejbronislaw.flowmeasure.model;

import java.util.ArrayList;
import java.util.List;

import jedrzejbronislaw.flowmeasure.settings.Consts;
import lombok.Getter;

public class Repository {
	
	@Getter
	private ProcessRepository currentProcessRepository;
	private List<ProcessRepository> processRepositories = new ArrayList<>();
	
	
	public ProcessRepository createNewProcessRepository(String name) {
		ProcessRepository processRepository = new ProcessRepository(Consts.FLOWMETERS_NUMBER, name);
		processRepository.getMetadata().setAuthor("unknown");
		
		processRepositories.add(processRepository);
		currentProcessRepository = processRepository;
		
		return processRepository;
	}
}
