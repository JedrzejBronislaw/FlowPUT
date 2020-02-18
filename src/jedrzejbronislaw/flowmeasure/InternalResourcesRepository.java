package jedrzejbronislaw.flowmeasure;

public class InternalResourcesRepository extends ResourcesRepository {

//	File jarDir1 = new File(ClassLoader.getSystemClassLoader().getResource("").getPath());
//	System.out.println("JAR: " + jarDir1.getAbsolutePath());
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
	
	
//	public String path(String resource) {
//		return MAIN_DIR + File.separator + resource;
//	}
//	
//	@Override
//	public URL getResourceURL(String resource) {
//		try {
//			return new URL(path(resource));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	public String getResourcePath(String resource) {
//		return getResourceURL(resource).toString();
//	}

//	@Override
//	public String getMainResourceLocation() {
//		return MAIN_DIR;
//	}

}
