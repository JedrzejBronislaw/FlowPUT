package jedrzejbronislaw.flowmeasure.tools.resourcesRepository;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.AccessLevel;
import lombok.Setter;

public abstract class ResourcesRepository {

	protected final static String DEFAULT_DIR_NAME = "res";
	public    final static String JAR_DIR = ClassLoader.getSystemClassLoader().getResource("").getPath();

	@Setter(value=AccessLevel.PROTECTED)
	private String MAIN_DIR;

	
	public ResourcesRepository(String mainDir) {
		MAIN_DIR = mainDir;
	}
	
	
	public String path(String resource) {
		return MAIN_DIR + "/" + resource;
	}
	
	public URL getResourceURL(String resource) {
		try {
			return new URL(path(resource));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getResourcePath(String resource) {
		return getResourceURL(resource).toString();
	}

	public String getMainResourceLocation() {
		return MAIN_DIR;
	}
}
