package jedrzejbronislaw.flowmeasure.tools.resourceAccess;

public class InternalResourceAccess extends ResourceAccess {

	public InternalResourceAccess() {
		super("");
	}

	public InternalResourceAccess build() {
		return build(null);
	}
	public InternalResourceAccess build(String resourceDirName) {
		String resourceName = "/" + ((resourceDirName == null) ? DEFAULT_DIR_NAME : resourceDirName);
		setMAIN_DIR(getClass().getResource(resourceName).toString());
		
		return this;
	}
}
