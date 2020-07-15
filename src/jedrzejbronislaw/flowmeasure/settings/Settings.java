package jedrzejbronislaw.flowmeasure.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import lombok.Getter;

public class Settings {
	
	private static final String settingsFileName = "properties.xml";
	private Properties defaultProperties;// = new Properties();
	
	private Properties properties;// = new Properties(defaultPropierties);
	private File file;
	
	
	
	@Getter
	private float pulsePerLitre = 450;

	@Getter
	private boolean bufferedData = false;

	@Getter
	private int bufferInterval = 1000;
	
	@Getter
	private String savePath = "";
	
	@Getter
	private String author = "";
	
	@Getter
	private String processName = "";
	
	
	
	public void setPulsePerLitre(float pulsePerLitre) {
		this.pulsePerLitre = pulsePerLitre;
		changeAction();
	}
	public void setBufferedData(boolean bufferedData) {
		this.bufferedData = bufferedData;
		changeAction();
	}
	public void setBufferInterval(int bufferInterval) {
		this.bufferInterval = bufferInterval;
		changeAction();
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
		changeAction();
	}
	public void setAuthor(String author) {
		this.author = author;
		changeAction();
	}
	public void setProcessName(String processName) {
		this.processName = processName;
		changeAction();
	}
	

	public Settings() {
		
		file = new File(settingsFileName);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		defaultProperties = new Properties();
		
		defaultProperties.setProperty("pulsePerLiter", "450");
		defaultProperties.setProperty("bufferedData","false");
		defaultProperties.setProperty("bufferInterval", "1000");
		
		properties = new Properties(defaultProperties);
	}
	
	
	
	public boolean read(){
		try(InputStream stream = new FileInputStream(file)){
			properties.loadFromXML(stream);
			
			pulsePerLitre = Float.parseFloat(properties.getProperty("pulsePerLitre"));
			bufferedData = Boolean.parseBoolean(properties.getProperty("bufferedData"));
			bufferInterval = Integer.parseInt(properties.getProperty("bufferInterval"));
			savePath = properties.getProperty("savePath");
		} catch (IOException e) {
			System.out.println("error read properties");
//			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean write() {
		try(OutputStream stream = new FileOutputStream(file)){
			
			properties.setProperty("pulsePerLitre",Float.toString(pulsePerLitre));
			properties.setProperty("bufferedData",Boolean.toString(bufferedData));
			properties.setProperty("bufferInterval",Integer.toString(bufferInterval));
			properties.setProperty("savePath",savePath);
			properties.storeToXML(stream, "properties");
		} catch (IOException e) {
			System.out.println("error write properties");
			return false;
		}
		
		return true;
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
