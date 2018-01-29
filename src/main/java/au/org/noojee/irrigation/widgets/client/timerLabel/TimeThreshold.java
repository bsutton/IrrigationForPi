package au.org.noojee.irrigation.widgets.client.timerLabel;

import java.io.Serializable;
import java.util.Arrays;

public class TimeThreshold implements Serializable
{
	// Logger logger = org.apache.logging.log4j.LogManager.getLogger();
	private static final long serialVersionUID = 1L;
	public long elapsedSeconds;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "TimeThreshold [elapsedSeconds=" + elapsedSeconds + ", message=" + message + ", mode=" + mode
				+ ", colours=" + Arrays.toString(colours) + ", startValue=" + startValue + "]";
	}

	public String message = "%%";
	public TimerLabelMode mode = TimerLabelMode.COUNT_UP;
	public String[] colours;
	public long startValue = 0;

	public TimeThreshold(long startValue, long elapsedSeconds, String message, TimerLabelMode mode, String[] colours)
	{
		this.startValue = startValue;
		this.elapsedSeconds = elapsedSeconds;
		this.message = message;
		this.mode = mode;
		this.colours = colours;
	}

	public TimeThreshold()
	{

	}
}
