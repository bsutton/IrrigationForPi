package au.org.noojee.irrigation.views;

import java.time.Duration;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.widgets.client.timerLabel.TimerLabelMode;
import au.org.noojee.irrigation.widgets.timerLabel.TimerLabelComponent;

class FeatureLine
{
	GardenFeature feature;
	Label label;
	Label timerLabel;
	Label duration;
	TimerLabelComponent timerDuration;
	
	HorizontalLayout parent;
	
	FeatureLine(GardenFeature feature, HorizontalLayout line)
	{
		this.feature = feature;
		this.parent = line;
	}
	
	public void setDurationLabel(Label durationLabel)
	{
		this.duration = durationLabel;
	}

	public void setLabel(Label label)
	{
		this.label = label;
		
	}
	
	boolean equals(GardenFeature feature)
	{
		return this.feature.equals(feature);
	}


	void showTimer(String timerMessage, Duration timer)
	{
		timerLabel = new Label(timerMessage);
		parent.replaceComponent(this.label, timerLabel);
		
		timerDuration = new TimerLabelComponent();
		
		parent.replaceComponent(duration, timerDuration);

		timerDuration.getBuilder().setStartValue(timer.getSeconds()).setThreshold(0)
				.setMode(TimerLabelMode.COUNT_DOWN).setMessage("%%").build();
		timerDuration.start();
	}
	
	void timerFinished()
	{
		// revert both the labels.
		parent.replaceComponent(timerLabel, label);
		parent.replaceComponent(timerDuration, duration);
	}

}