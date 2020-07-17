package jedrzejbronislaw.flowmeasure.settings;

import jedrzejbronislaw.flowmeasure.tools.SimpleListenerManager;

public class Settings implements PropertyAccess {
	
	private static final String settingsFileName = "properties.xml";
	private final PropertyFile propertyFile;
	private final Properties properties = new Properties();
	private final SimpleListenerManager listenerManager = new SimpleListenerManager();
	
	public Settings() {
		properties.add(AppProperties.values());
		properties.setChangeAction(listenerManager::action);
		
		propertyFile = new PropertyFile(
				settingsFileName,
				AppProperties.values(),
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
	public void set(PropertyDesc name, String value) {
		properties.set(name, value);
	}
	
	@Override
	public Property get(PropertyDesc name) {
		return properties.get(name);
	}
	
	
	public void addChangeListiner(Runnable listener) {
		listenerManager.add(listener);
	}
}
