package jedrzejbronislaw.flowmeasure;

public class SideDirResourcesRepository extends ResourcesRepository {

	private final static String DEFAULT_DIR_NAME = "res";
	private final static String JAR_DIR = "file:" + ClassLoader.getSystemClassLoader().getResource("").getPath();
	
	public SideDirResourcesRepository(String resourceDirName) {
		super(JAR_DIR + ((resourceDirName != null) ? resourceDirName : DEFAULT_DIR_NAME));
	}
	

}
