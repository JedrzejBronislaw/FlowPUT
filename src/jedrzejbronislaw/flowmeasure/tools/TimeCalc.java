package jedrzejbronislaw.flowmeasure.tools;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeCalc {

	public static String createDurationString(LocalDateTime startTime, LocalDateTime endTime) {
		return createDurationString(startTime, endTime, " ");
	}
	public static String createDurationString(LocalDateTime startTime, LocalDateTime endTime, String separator) {
		StringBuffer output = new StringBuffer();
		
		if (startTime == null || endTime == null) return "";
		
//		long years = ChronoUnit.YEARS.between(startTime, endTime);
//		long months = ChronoUnit.MONTHS.between(startTime, endTime);
		long days = ChronoUnit.DAYS.between(startTime, endTime);

		long hours = ChronoUnit.HOURS.between(startTime, endTime);
		long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
		long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
		
		if (days > 0)		
			output.append(days +" days" + separator);
		if ((hours%24) > 0)		
			output.append((hours%24) +" hours" + separator);
		if ((minutes%60) > 0)		
			output.append((minutes%60) +" minutes" + separator);
		if ((seconds%60) > 0)		
			output.append((seconds%60) +" seconds" + separator); //TODO internationalization
		
		output.delete(output.length() - separator.length(), output.length());
		
		return output.toString();
	}
	
	public static long durationSeconds(LocalDateTime startTime, LocalDateTime endTime) {
		return ChronoUnit.SECONDS.between(startTime, endTime);		
	}
}
