package jedrzejbronislaw.flowmeasure;

public class SideDirResourcesRepository extends ResourcesRepository {
	
	public SideDirResourcesRepository(String resourceDirName) {
		super("file:" + JAR_DIR + ((resourceDirName != null) ? resourceDirName : DEFAULT_DIR_NAME));
	}
}
