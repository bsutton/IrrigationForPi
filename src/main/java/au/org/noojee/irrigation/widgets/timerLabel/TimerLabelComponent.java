package au.org.noojee.irrigation.widgets.timerLabel;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.vaadin.server.Page;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

import au.org.noojee.irrigation.widgets.client.timerLabel.TimeThreshold;
import au.org.noojee.irrigation.widgets.client.timerLabel.TimerLabelClientRpc;
import au.org.noojee.irrigation.widgets.client.timerLabel.TimerLabelMode;
import au.org.noojee.irrigation.widgets.client.timerLabel.TimerLabelServerRpc;
import au.org.noojee.irrigation.widgets.client.timerLabel.TimerLabelState;

public class TimerLabelComponent extends AbstractComponent
{
	private static final long serialVersionUID = -3987637508482459798L;
	// NO_UCD
	Logger logger = org.apache.logging.log4j.LogManager.getLogger();

	UI ui = UI.getCurrent();

	private final TimerLabelServerRpc rpc = new TimerLabelServerRpc()
	{
		private static final long serialVersionUID = 2255151104825789029L;

	};

	public TimerLabelComponent()
	{
		registerRpc(rpc);

	}

	Long getBrowserEpoc()
	{
		final Page page = ui.getPage();
		return page.getWebBrowser().getCurrentDate().getTime();
	}

	/**
	 * set the initial time, format and mode
	 * 
	 * @param startValueSeconds
	 * @param formatter
	 * @param mode
	 * @param format
	 */
	public void setInitialValue(final TimeThreshold[] thresholds)
	{
		getState().thresholds = thresholds;

		getRpcProxy(TimerLabelClientRpc.class).setInitialValue(thresholds);

	}

	public SetStartValue getBuilder()
	{
		return new Builder();
	}

	public interface SetStartValue extends SetThreshold
	{
		SetThreshold setStartValue(long startValue);
	}

	public interface AddColour extends SetMode, SetMessage
	{
		AddColour addColour(String colour);
	}

	public interface SetThreshold
	{
		AddColour setThreshold(long threshold);
	}

	public interface SetMode
	{
		SetMessage setMode(TimerLabelMode mode);
	}

	public interface SetMessage
	{
		Done setMessage(String message);
	}

	public interface Done
	{
		SetStartValue addThreshold();

		void build();
	}

	public class Builder implements SetThreshold, AddColour, SetMode, SetMessage, Done, SetStartValue
	{
		List<TimeThreshold> thresholds = new LinkedList<>();
		TimeThreshold threshold = new TimeThreshold();

		SetStartValue getBuilder()
		{

			return new Builder();
		}

		@Override
		public SetStartValue addThreshold()
		{
			if (threshold.colours == null)
			{
				threshold.colours = new String[] { "black" };
			}
			thresholds.add(threshold);
			threshold = new TimeThreshold();
			return this;
		}

		@Override
		public void build()
		{
			if (threshold.colours == null)
			{
				threshold.colours = new String[] { "black" };
			}
			thresholds.add(threshold);

			setInitialValue(thresholds.toArray(new TimeThreshold[0]));
		}

		@Override
		public SetMessage setMode(final TimerLabelMode mode)
		{
			threshold.mode = mode;
			return this;
		}

		@Override
		public AddColour addColour(final String colour)
		{
			if (threshold.colours == null)
			{
				threshold.colours = new String[0];
			}
			final String[] colours = new String[threshold.colours.length + 1];
			int i = 0;
			for (final String col : threshold.colours)
			{
				colours[i++] = col;
			}
			colours[i++] = colour;
			threshold.colours = colours;
			return this;
		}

		@Override
		public AddColour setThreshold(final long threshold)
		{
			this.threshold.elapsedSeconds = threshold;
			return this;
		}

		@Override
		public Done setMessage(final String message)
		{
			threshold.message = message;
			return this;
		}

		@Override
		public SetThreshold setStartValue(final long startValue)
		{
			threshold.startValue = startValue;
			return this;
		}

	}

	/**
	 * start the timer running
	 */
	public void start()
	{

		getState().epoc = getBrowserEpoc();

		getState().started = true;
		getRpcProxy(TimerLabelClientRpc.class).start();

	}

	/**
	 * stop the timer running
	 */
	public void stop()
	{

		// long elapsedSeconds = 0;
		// if (getState().epoc != null)
		// {
		// elapsedSeconds = (getBrowserEpoc() - getState().epoc) / 1000;
		// }
		// long value = getState().mode.getValue(elapsedSeconds,
		// getState().startValue);
		// getState().startValue = value;
		// getState().started = false;

		getRpcProxy(TimerLabelClientRpc.class).stop();

	}

	@Override
	public void setStyleName(final String style)
	{
		super.setStyleName(style);
		getState().style = style;
	}

	@Override
	public TimerLabelState getState()
	{
		return (TimerLabelState) super.getState();
	}

}
