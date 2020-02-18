package jedrzejbronislaw.flowmeasure;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.AccessLevel;
import lombok.Setter;

public abstract class ResourcesRepository {

	@Setter(value=AccessLevel.PROTECTED)
	private String MAIN_DIR;

//	abstract String getMainResourceLocation();
	
	
	public ResourcesRepository(String mainDir) {
		MAIN_DIR = mainDir;

	}
//	URL getResourceURL(String resource);
//	String getResourcePath(String resource);
	
	
	
	public String path(String resource) {
		return MAIN_DIR + File.separator + resource;
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
