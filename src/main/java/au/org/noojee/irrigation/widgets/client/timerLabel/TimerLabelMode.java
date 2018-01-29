package au.org.noojee.irrigation.widgets.client.timerLabel;

public enum TimerLabelMode
{

	COUNT_UP
	{
		@Override
		public long getValue(long elapsedSeconds, long startValue)
		{
			return startValue + elapsedSeconds;
		}
	},
	COUNT_DOWN
	{
		@Override
		public long getValue(long elapsedSeconds, long startValue)
		{
			// don't allow the count down to go negative
			return Math.max(0, startValue - elapsedSeconds);
		}
	};

	public abstract long getValue(long elapsedSeconds, long startValue);

}
