package jedrzejbronislaw.flowmeasure.tools.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;

public class PropertyFile {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private final Properties properties;
	private final File file;
	
	private final PropertyDesc[] propertyNames;
	
	private final BiConsumer<PropertyDesc, String> propertySetter;
	private final Function<PropertyDesc, String> propertyGetter;
	
	public PropertyFile(String fileName,
			PropertyDesc[] propertyNames,
			BiConsumer<PropertyDesc, String> propertySetter,
			Function<PropertyDesc, String> propertyGetter) {
		
		this.propertyNames = propertyNames;
		this.propertySetter = propertySetter;
		this.propertyGetter = propertyGetter;
		
		properties = new Properties();
		
		file = new File(fileName);
		createFileIfNotExists(file);
	}

	private void createFileIfNotExists(File file) {
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public boolean read() {
		try(InputStream stream = new FileInputStream(file)) {
			properties.loadFromXML(stream);
			
			for (PropertyDesc name : propertyNames)
				propertySetter.accept(name, properties.getProperty(name.getName()));
			
		} catch (IOException e) {
			log.warn("error read properties");
			return false;
		}
		
		return true;
	}

	public boolean write() {
		try(OutputStream stream = new FileOutputStream(file)) {
			
			for (PropertyDesc name : propertyNames)
				properties.setProperty(name.getName(), propertyGetter.apply(name));
				
			properties.storeToXML(stream, "properties");
		} catch (IOException e) {
			log.warn("error write properties");
			return false;
		}
		
		return true;
	}
}
