package au.org.noojee.irrigation.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeHelper
{
	static private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public static LocalDateTime Max(LocalDateTime lhs, LocalDateTime rhs)
	{
		if (lhs == null)
			return rhs;
		if (rhs == null)
			return lhs;
		
		if (lhs.isAfter(rhs))
				return lhs;
		else
			return rhs;
	}
	
	public static LocalDateTime Min(LocalDateTime lhs, LocalDateTime rhs)
	{
		if (lhs == null)
			return rhs;
		if (rhs == null)
			return lhs;

		if (lhs.isBefore(rhs))
				return lhs;
		else
			return rhs;
	}

	/**
	 * Checks if the LocalDateTime is null or is set to zero.
	 *  
	 * @param dateTimeStarted
	 * @return true if null or zero.
	 */
	public static boolean isEmpty(LocalDateTime dateTime)
	{
		return dateTime == null || dateTime == Constants.DATETIMEZERO ;
	}

	public static String format(LocalDateTime dateTimeStarted)
	{
		return dateTimeStarted.format(formatter);
	}
}
