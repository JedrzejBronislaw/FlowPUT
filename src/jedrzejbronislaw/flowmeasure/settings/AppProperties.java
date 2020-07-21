package jedrzejbronislaw.flowmeasure.settings;

import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyDesc;
import jedrzejbronislaw.flowmeasure.tools.settings.poperties.PropertyType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppProperties implements PropertyDesc {
	
	BUFFERED_DATA  ("bufferedData",   PropertyType.BOOL,   "false"),
	BUFFER_INTERVAL("bufferInterval", PropertyType.INT,    "1000"),
	SAVE_PATH      ("savePath",       PropertyType.STRING, ""),
	AUTHOR         ("author",         PropertyType.STRING, ""),
	PROCESS_NAME   ("processName",    PropertyType.STRING, "");
	
	private final String name;
	private final PropertyType type;
	private final String defaultValue;
}
