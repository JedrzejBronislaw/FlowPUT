package jedrzejbronislaw.flowmeasure.settings;

import jedrzejbronislaw.flowmeasure.tools.SimpleListenerManager;

public class Settings implements PropertyAccess {
	
	private static final String settingsFileName = "properties.xml";
	private final PropertyFile propertyFile;
	private final Properties properties = new Properties();
	private final SimpleListenerManager listenerManager = new SimpleListenerManager();
	
	public Settings() {
		
		properties.add(PropertyName.PULSE_PER_LITRE, new FloatProperty("450"));
		properties.add(PropertyName.BUFFERED_DATA,   new BoolProperty("false"));
		properties.add(PropertyName.BUFFER_INTERVAL, new IntProperty("1000"));
		properties.add(PropertyName.SAVE_PATH,       new StringProperty(""));
		properties.add(PropertyName.AUTHOR,          new StringProperty(""));
		properties.add(PropertyName.PROCESS_NAME,    new StringProperty(""));
		
		properties.setChangeAction(listenerManager::action);
		
		
		propertyFile = new PropertyFile(
				settingsFileName,
				properties::setProperty,
				properties::getPropertyValue);
	}

	
	public boolean saveToFile() {
		return propertyFile.write();
	}
	
	public boolean loadFromFile() {
		return propertyFile.read();
	}
	
	
	@Override
	public void set(PropertyName name, String value) {
		properties.set(name, value);
	}
	
	@Override
	public Property get(PropertyName name) {
		return properties.get(name);
	}
	
	
	public void addChangeListiner(Runnable listener) {
		listenerManager.add(listener);
	}
}
