package jedrzejbronislaw.flowmeasure;

public class InternalResourcesRepository extends ResourcesRepository {

	private final static String DEFAULT_DIR_NAME = "res";
	
	public InternalResourcesRepository() {
		super("");
	}
	
	public InternalResourcesRepository build(String resourceDirName) {
		System.out.println(" -> " + getClass().getResource("/").toString());
		System.out.println(" -> " + getClass().getResource("/res").toString());
		if(resourceDirName != null)
			setMAIN_DIR(getClass().getResource("/res").toString());
		else
			setMAIN_DIR(getClass().getResource("/" + DEFAULT_DIR_NAME).toString());
		return this;
	}
	
}
