package au.org.noojee.irrigation.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Constants
{
	static public LocalDate DATE1970 = LocalDate.of(1970, 1, 1);
	
	static public LocalDateTime DATETIME1970 = LocalDateTime.of(1970, 1, 1,0,0,0);
	
	// With LocalDate's there is no easy way of passing a date value of '0'
	// 1/1/1970 gives the desired result.
	static public LocalDate DATEZERO = DATE1970;
	static public LocalDateTime DATETIMEZERO = DATETIME1970;
	
	
	static public Duration FIFTEEN_MINUTES = Duration.ofMinutes(15);

}
