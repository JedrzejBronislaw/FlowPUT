package jedrzejbronislaw.flowmeasure;

public class InternalResourcesRepository extends ResourcesRepository {

	public InternalResourcesRepository() {
		super("");
	}

	public InternalResourcesRepository build() {
		return build(null);
	}
	public InternalResourcesRepository build(String resourceDirName) {
		String resourceName = "/" + ((resourceDirName == null) ? DEFAULT_DIR_NAME : resourceDirName);
		setMAIN_DIR(getClass().getResource(resourceName).toString());
		
		return this;
	}
}
