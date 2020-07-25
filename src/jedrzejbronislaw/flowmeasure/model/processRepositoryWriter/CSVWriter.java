package jedrzejbronislaw.flowmeasure.model.processRepositoryWriter;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CSVWriter {
	
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final String SEPARATOR = ";";
	public static final String NEW_LINE_SEPARATOR = "\n";
	
	private final Writer writer;
	
	
	public void write(String text) throws IOException {
		writer.write(text);
	};
	
	public void writeWithSeparator(String text) throws IOException {
		writer.write(text + SEPARATOR);
	};
	
	public void writeWithSeparator(int value) throws IOException {
		writeWithSeparator(Integer.toString(value));
	};
	
	public void writeSeparator() throws IOException {
		writer.write(SEPARATOR);
	};
	
	public void writeSeparators(int number) throws IOException {
		for (int i=0; i<number; i++)
			writer.write(SEPARATOR);
	}
	
	public void newLine() throws IOException{
		writer.write(NEW_LINE_SEPARATOR);
	}

	public void line(String text) throws IOException {
		writer.write(text);
		newLine();
	}

	public void property(String propertyName, String value) throws IOException {
		writeWithSeparator(propertyName);
		if(value != null) writer.write(value);
		newLine();
	}

	public void property(String propertyName, LocalDateTime datatime) throws IOException {
		if(datatime == null)
			property(propertyName, ""); else
			property(propertyName, datatime.format(FORMATTER));
	}

	public void property(String propertyName, int value) throws IOException {
		property(propertyName, Integer.toString(value));
	}

	public void property(String propertyName, float value) throws IOException {
		property(propertyName, Float.toString(value));
	}

	public void close() throws IOException {
		writer.close();
	}
}
