package jedrzejbronislaw.flowmeasure.settings;

public enum PropertyName {
	PULSE_PER_LITRE("pulsePerLitre"),
	BUFFERED_DATA("bufferedData"),
	BUFFER_INTERVAL("bufferInterval"),
	SAVE_PATH("savePath"),
	AUTHOR("author"),
	PROCESS_NAME("processName");
	
	String name;

	PropertyName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
