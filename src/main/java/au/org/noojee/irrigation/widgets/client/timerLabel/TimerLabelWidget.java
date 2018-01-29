package au.org.noojee.irrigation.widgets.client.timerLabel;

import java.util.Date;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;

public class TimerLabelWidget extends Label
{

	private static final int UPDATE_INTERVAL = 500;

	private TimerLabelServerRpc rpc;

	Boolean started = false;

	TimeThreshold[] thresholds;

	int colourIndex = 0;

	String style;

	/**
	 * time at which the timer started running, used to calculate an offset from
	 * the startValue
	 */
	Long epoc;

	private Timer timer;

	public TimerLabelWidget()
	{

		addAttachHandler(new Handler()
		{

			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (!event.isAttached() && timer != null)
				{
					timer.cancel();
				}

			}
		});
	}

	public void setInitialValue(TimeThreshold[] thresholds)
	{

		TimeThreshold threshold = getActiveThreshold(0L);
		long value = threshold.mode.getValue(0, threshold.startValue);
		colourIndex = 0;
		colourIndex %= threshold.colours.length;
		getElement().setInnerHTML(getFormatted(value, threshold.message, threshold.colours, colourIndex));
	}

	/**
	 * we assume the thresholds are in order
	 * 
	 * @param elapsedSeconds
	 * @return
	 */
	TimeThreshold getActiveThreshold(long elapsedSeconds)
	{
		TimeThreshold selected = thresholds[0];
		for (TimeThreshold threshold : thresholds)
		{
			if (elapsedSeconds < threshold.elapsedSeconds)
			{
				break;
			}
			if (elapsedSeconds > threshold.elapsedSeconds)
			{

				selected = threshold;
			}
		}
		return selected;
	}

	public void start()
	{
		start(UPDATE_INTERVAL);
	}

	private void start(final int delay)
	{
		if (timer != null)
		{
			timer.cancel();
		}
		timer = new Timer()
		{

			@Override
			public void run()
			{
				if (epoc != null)
				{
					long elapsedSeconds = (new Date().getTime() - epoc) / 1000;
					TimeThreshold threshold = getActiveThreshold(elapsedSeconds);
					long value = threshold.mode.getValue(elapsedSeconds - threshold.elapsedSeconds,
							threshold.startValue);
					colourIndex++;
					colourIndex %= threshold.colours.length;

					getElement().setInnerHTML(getFormatted(value, threshold.message, threshold.colours, colourIndex));
					timer.schedule(delay);

				}
			}

		};
		timer.run();

	}

	public void stop()
	{
		timer.cancel();

	}

	public void restoreState(TimerLabelState timerLabelState)
	{
		style = timerLabelState.style;
		thresholds = timerLabelState.thresholds;
		if (timer != null)
		{
			timer.cancel();
		}

		// The widget may have been destroyed, so we need to rebuild it...
		if (started != null)
		{
			console("TimerLabelWidget: Restoring state +" + this.hashCode());
			if (started)
			{
				start(UPDATE_INTERVAL);
			}
			else
			{
				long elapsedSeconds = (new Date().getTime() - timerLabelState.epoc) / 1000;

				TimeThreshold threshold = getActiveThreshold(elapsedSeconds);
				long value = threshold.mode.getValue(elapsedSeconds, threshold.startValue);
				colourIndex = 0;
				colourIndex %= threshold.colours.length;

				getElement().setInnerHTML(getFormatted(value, threshold.message, threshold.colours, colourIndex));
			}
		}
		else
		{
			console("TimerLabelWidget: Started is null " + this.hashCode());
		}

	}

	public static native void console(String text)
	/*-{
	   try{
	    console.log(text);
	    }catch (err)
	    {
	    // IE compatibility, console doesn't exist if the developer tools haven't been opened
	    }
	}-*/;

	/**
	 * @return the epoc
	 */
	public Long getEpoc()
	{
		return epoc;
	}

	// native GWT widget!

	/**
	 * @param epoc
	 *            the epoc to set
	 */
	public void setEpoc(Long epoc)
	{
		this.epoc = epoc;
	}

	public void setRpc(TimerLabelServerRpc prpc)
	{
		// very dodgy assigning to static field
		rpc = prpc;

	}

	/**
	 * @param started
	 *            the started to set
	 */
	public void setStarted(Boolean started)
	{
		this.started = started;
	}

	public void setThresholds(TimeThreshold[] thresholds)
	{
		this.thresholds = thresholds;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	String getFormatted(Long seconds, String message, String[] colourList, int colourIndex)
	{
		String text = formatTime(seconds);

		text = message.replace("%%", text);

		text = "<font style='" + style + "' color='" + colourList[colourIndex] + "'><b>" + text + "</b></font>";

		return text;
	}

	String formatTime(Long seconds)
	{

		long absSeconds = Math.abs(seconds);
		if (absSeconds == 0)
		{
			return "---";
		}

		NumberFormat numberFormat = NumberFormat.getFormat("00");

		int secs = (int) (absSeconds % 60);
		int mins = (int) ((absSeconds / 60) % 60);
		int hours = (int) ((absSeconds / 3600) % 24);
		int days = (int) (absSeconds / (3600 * 24));

		String time = "";
		if (days > 0)
		{
			time += days + ":";
		}
		if (hours > 0 || days > 0)
		{
			time += numberFormat.format(hours) + ":";
		}
		time += numberFormat.format(mins) + ":";
		time += numberFormat.format(secs);
		return time;
	}

}
