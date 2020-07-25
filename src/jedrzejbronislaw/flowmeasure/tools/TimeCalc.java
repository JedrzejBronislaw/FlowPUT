package jedrzejbronislaw.flowmeasure.tools;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeCalc {

	public static String createDurationString(LocalDateTime startTime, LocalDateTime endTime) {
		return createDurationString(startTime, endTime, " ");
	}
	public static String createDurationString(LocalDateTime startTime, LocalDateTime endTime, String separator) {
		if (startTime == null || endTime == null) return "";
		
		StringBuffer output = new StringBuffer();
		
		long days       = ChronoUnit.DAYS.   between(startTime, endTime);
		long allHours   = ChronoUnit.HOURS.  between(startTime, endTime);
		long allMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
		long allSeconds = ChronoUnit.SECONDS.between(startTime, endTime);

		int hours   = (int) (allHours   % 24);
		int minutes = (int) (allMinutes % 60);
		int seconds = (int) (allSeconds % 60);
		
		if (days > 0)    output.append(days    + " days"    + separator);
		if (hours > 0)   output.append(hours   + " hours"   + separator);
		if (minutes > 0) output.append(minutes + " minutes" + separator);
		if (seconds > 0) output.append(seconds + " seconds" + separator);
		
		int startDel = output.length() - separator.length();
		if (startDel >= 0)
			output.delete(startDel, output.length());
		
		return output.toString();
	}
	
	public static long durationSeconds(LocalDateTime startTime, LocalDateTime endTime) {
		return ChronoUnit.SECONDS.between(startTime, endTime);		
	}
}
