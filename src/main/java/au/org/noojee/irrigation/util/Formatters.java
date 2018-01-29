package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.time.DurationFormatUtils;

public interface Formatters
{

	static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/YYYY");

	static final DateTimeFormatter dateFormatTime = DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mma");

	public static String format(LocalDate date)
	{
		return (date == null ? "" : date.format(dateFormat));
	}

	public static String format(LocalDateTime dateTime)
	{
		return (dateTime == null ? "" : dateTime.format(dateFormatTime));
	}

	/**
	 * Formats the Duration to H:mm
	 * 
	 * @param duration
	 * @return a blank string if duration is null otherwise the duration as per the format.
	 */
	public static String format(Duration duration)
	{

		if (duration.toMillis() >= 60000)

			return (duration == null ? "" : DurationFormatUtils.formatDuration(duration.toMillis(), "H:mm"));
		else
			return (duration == null ? "" : DurationFormatUtils.formatDuration(duration.toMillis(), "s") + " secs");

	}

	/**
	 * Formats the Duration to the given format. Formats support are any supported by
	 * {@link DurationFormatUtils.formatDuration}
	 * 
	 * @param duration
	 * @param format to render duration to.
	 * @return a blank string if duration is null otherwise the duration as per the format.
	 */
	public static String format(Duration duration, String format)
	{

		return (duration == null ? "" : DurationFormatUtils.formatDuration(duration.toMillis(), format));
	}

}
