package jedrzejbronislaw.flowmeasure;

public class SideDirResourcesRepository extends ResourcesRepository {

//	File jarDir1 = new File(ClassLoader.getSystemClassLoader().getResource("").getPath());
//	System.out.println("JAR: " + jarDir1.getAbsolutePath());
	private final static String DEFAULT_DIR_NAME = "res";
	private final static String JAR_DIR = "file:" + ClassLoader.getSystemClassLoader().getResource("").getPath() /*+ File.separator*/;// + DIR_NAME;
	
	public SideDirResourcesRepository(String resourceDirName) {
		super(JAR_DIR + ((resourceDirName != null) ? resourceDirName : DEFAULT_DIR_NAME));
//		String dirName = (resourceDirName != null) ? resourceDirName : DEFAULT_DIR_NAME;
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
