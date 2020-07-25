package jedrzejbronislaw.flowmeasure.tools.resourceAccess;

public class SideDirResourceAccess extends ResourceAccess {
	
	public SideDirResourceAccess(String resourceDirName) {
		super("file:" + JAR_DIR + ((resourceDirName != null) ? resourceDirName : DEFAULT_DIR_NAME));
	}
}
