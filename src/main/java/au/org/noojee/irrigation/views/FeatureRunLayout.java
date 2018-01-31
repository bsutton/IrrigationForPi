package au.org.noojee.irrigation.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.server.Responsive;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import au.org.noojee.irrigation.controllers.Timer;
import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.entities.History;
import au.org.noojee.irrigation.util.Formatters;
import au.org.noojee.irrigation.widgets.client.timerLabel.TimerLabelMode;
import au.org.noojee.irrigation.widgets.timerLabel.TimerLabelComponent;

class FeatureRunLayout extends HorizontalLayout
{
	private static Logger logger = LogManager.getLogger();
	
	private static final long serialVersionUID = 1L;
	static final int LAST_WIDTH = 28;
	static final int DURATION_WIDTH = 22;

	GardenFeature feature;

	UI ui;

	FeatureRunLayout(GardenFeature feature)
	{
		ui = UI.getCurrent();

		this.feature = feature;
		// this.setWidth("100%");
		showHistory(feature.getLastEvent());
	}

	boolean equals(GardenFeature feature)
	{
		return this.feature.equals(feature);
	}

	void showTimer(Timer timer)
	{
		if (timer == null)
		{
			RuntimeException e = new RuntimeException("Timer may not be null");
			logger.error(e,e);
			throw e;
		}
		
		ui.access(() ->
			{
				this.removeAllComponents();

				Label timerLabel = new Label(timer.getDescription());
				timerLabel.setStyleName("i4p-label", true);

				this.addComponent(timerLabel);

				TimerLabelComponent timerDuration = new TimerLabelComponent();

				this.addComponent(timerDuration);

				timerDuration.getBuilder()
						.setStartValue(timer.timeRemaining().getSeconds())
						.setThreshold(0)
						.setMode(TimerLabelMode.COUNT_DOWN)
						.setMessage("%%")
						.build();
				timerDuration.setStyleName("i4p-label", true);

				Responsive.makeResponsive(timerLabel, timerDuration);

				timerDuration.start();
			});
	}

	void timerFinished()
	{
		History event = feature.getLastEvent();
		showHistory(event);

	}

	void showHistory(History history)
	{
		ui.access(() ->
			{
				this.removeAllComponents();

				if (history != null)
				{

					Label lastWateredLabel = new Label(Formatters.format(history.getStart()));
					this.addComponent(lastWateredLabel);

					lastWateredLabel.setWidth(LAST_WIDTH, Unit.MM);
					lastWateredLabel.setStyleName("i4p-label");
					Responsive.makeResponsive(lastWateredLabel);

					Label durationLabel = new Label(Formatters.format(history.getDuration()));
					this.addComponent(durationLabel);

					durationLabel.setWidth(DURATION_WIDTH, Unit.MM);
					durationLabel.setStyleName("i4p-label");
					Responsive.makeResponsive(durationLabel);
				}

			});
	}

}