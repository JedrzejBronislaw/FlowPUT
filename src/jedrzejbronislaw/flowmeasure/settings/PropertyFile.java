package jedrzejbronislaw.flowmeasure.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyFile {

	private final Properties properties;
	private final File file;
	
	private final BiConsumer<PropertyName, String> propertySetter;
	private final Function<PropertyName, String> propertyGetter;
	
	public PropertyFile(String fileName, BiConsumer<PropertyName, String> propertySetter, Function<PropertyName, String> propertyGetter) {
		
		this.propertySetter = propertySetter;
		this.propertyGetter = propertyGetter;
		
		properties = new Properties();
		
		file = new File(fileName);
		createFileIfNotExists(file);
	}

	private void createFileIfNotExists(File file) {
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public boolean read(){
		try(InputStream stream = new FileInputStream(file)){
			properties.loadFromXML(stream);
			
			for (PropertyName name : PropertyName.values())
				propertySetter.accept(name, properties.getProperty(name.toString()));
			
		} catch (IOException e) {
			System.out.println("error read properties");
			return false;
		}
		
		return true;
	}

	public boolean write() {
		try(OutputStream stream = new FileOutputStream(file)){
			
			for (PropertyName name : PropertyName.values())
				properties.setProperty(name.toString(),  propertyGetter.apply(name));
				
			properties.storeToXML(stream, "properties");
		} catch (IOException e) {
			System.out.println("error write properties");
			return false;
		}
		
		return true;
	}
}
