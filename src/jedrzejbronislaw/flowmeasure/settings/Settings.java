package jedrzejbronislaw.flowmeasure.settings;

import jedrzejbronislaw.flowmeasure.tools.SimpleListenerManager;
import jedrzejbronislaw.flowmeasure.tools.settings.PropertyAccess;
import jedrzejbronislaw.flowmeasure.tools.settings.PropertyFile;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.Properties;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.Property;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;

public class Settings implements PropertyAccess {
	
	private static final String settingsFileName = "properties.xml";
	private final PropertyFile propertyFile;
	private final Properties properties = new Properties();
	private final SimpleListenerManager listenerManager = new SimpleListenerManager();
	
	public Settings() {
		
		PropertyDesc[] propertyDesc = PropertyDesc.sum(
			RatioProperty.generate(6),
			AppProperties.values()
		);
		
		
		properties.add(propertyDesc);
		properties.setChangeAction(listenerManager::action);
		
		propertyFile = new PropertyFile(
				settingsFileName,
				propertyDesc,
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
