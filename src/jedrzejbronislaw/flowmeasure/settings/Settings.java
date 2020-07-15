package jedrzejbronislaw.flowmeasure.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Settings {
	
	private static final String settingsFileName = "properties.xml";
	private PropertyFile propertyFile;
	
	private Map<String, Property> propertyMap = new HashMap<>();

	private void addProperty(PropertyName name, Property property) {
		propertyMap.put(name.toString(), property);
	}
	
	public String getString(PropertyName name) {
		return getPropertyValue(name);
	}
	
	public boolean getBool(PropertyName name) {
		return getPropertyBoolValue(name).get();
	}
	
	public int getInt(PropertyName name) {
		return getPropertyIntValue(name).get();
	}
	
	public float getFloat(PropertyName name) {
		return getPropertyFloatValue(name).get();
	}
	
	public String getPropertyValue(PropertyName name) {
		Property property = propertyMap.get(name.toString());
		
		return property==null ? null : property.toString();
	}
	
	public Optional<Integer> getPropertyIntValue(PropertyName name) {
		IntProperty property;
		
		try {
			property = (IntProperty) getProperty(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}
	
	public Optional<Float> getPropertyFloatValue(PropertyName name) {
		FloatProperty property;
		
		try {
			property = (FloatProperty) getProperty(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}
	
	public Optional<Boolean> getPropertyBoolValue(PropertyName name) {
		BoolProperty property;
		
		try {
			property = (BoolProperty) getProperty(name);
		} catch	(ClassCastException e) {
			return Optional.empty();
		}
		
		return Optional.of(property.get());
	}
	
	public Property getProperty(PropertyName name) {
		return propertyMap.get(name.toString());
	}
	
	public void setProperty(PropertyName name, String value) {
		Property property = propertyMap.get(name.toString());

		if(property != null && value != null && property.set(value))
			changeAction();
	}
	
	public void setProperty(PropertyName name, int value) {
		setProperty(name, Integer.toString(value));
	}
	
	public void setProperty(PropertyName name, float value) {
		setProperty(name, Float.toString(value));
	}
	
	public void setProperty(PropertyName name, boolean value) {
		setProperty(name, Boolean.toString(value));
	}
	
	
	public Settings() {
		
		addProperty(PropertyName.PULSE_PER_LITRE, new FloatProperty("450"));
		addProperty(PropertyName.BUFFERED_DATA,   new BoolProperty("false"));
		addProperty(PropertyName.BUFFER_INTERVAL, new IntProperty("1000"));
		addProperty(PropertyName.SAVE_PATH,       new StringProperty(""));
		addProperty(PropertyName.AUTHOR,          new StringProperty(""));
		addProperty(PropertyName.PROCESS_NAME,    new StringProperty(""));
		
		
		propertyFile = new PropertyFile(
				settingsFileName,
				this::setProperty,
				this::getPropertyValue);
	}
	

	public boolean saveToFile() {
		return propertyFile.write();
	}
	
	public boolean loadFromFile() {
		return propertyFile.read();
	}
	
	
	
	private List<Runnable> changeListeners = new ArrayList<>();

	
	private void changeAction() {
		changeListeners.forEach(listiner -> listiner.run());
	}
	
	public void addChangeListiner(Runnable listener) {
		changeListeners.add(listener);
		listener.run();
	}
	
	/*
	public boolean setBufferInterval(int bufferInterval) {
		if(bufferedFlowConsumer != null) {
			this.bufferInterval = bufferInterval;
			bufferedFlowConsumer.setInterval(bufferInterval);
			return true;
		} else
			return false;
	}
	
	@Getter
	private FlowMeasurementConsumer flowConsumer;
	
	public void setPlainFlowMeasurementConsumer() {
		flowConsumer = plainFlowConsumer;
		bufferedData = false;
	}
	public void setBufferedFlowMeasurementConsumer() {
		flowConsumer = bufferedFlowConsumer;
		bufferedData = true;
	}
	

	@Setter
	private ProcessRepository plainFlowConsumer;

//	@Setter
	private DataBuffer bufferedFlowConsumer;
	
	public void setBufferedFlowConsumer(DataBuffer bufferedFlowConsumer) {
		this.bufferedFlowConsumer = bufferedFlowConsumer;
		bufferInterval = bufferedFlowConsumer.getInterval();
	}
*/
}
