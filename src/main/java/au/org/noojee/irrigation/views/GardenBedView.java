package au.org.noojee.irrigation.views;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.controllers.EndPointBus;
import au.org.noojee.irrigation.controllers.Timer;
import au.org.noojee.irrigation.controllers.TimerControl;
import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.entities.GardenFeature;

public class GardenBedView extends VerticalLayout
		implements SmartView, EndPointChangeListener, ViewChangeListener, TimerNotification
{

	private static final int SWITCH_WIDTH = 35;
	private static final long serialVersionUID = 1L;
	public static final String NAME = "GardenBeds";
	public static final String LABEL = "Garden Beds";

	private Map<EndPoint, Switch> switchMap = new HashMap<>();
	private List<FeatureRunLayout> featureLines;

	private UI ui;

	public GardenBedView()
	{
		this.ui = UI.getCurrent();
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		this.removeAllComponents();

		HorizontalLayout heading = new HorizontalLayout();
		heading.setWidth("100%");
		Label headingLabel = new Label("Garden Beds");
		headingLabel.setStyleName("i4p-heading");
		Responsive.makeResponsive(headingLabel);
		heading.addComponent(headingLabel);
		heading.setComponentAlignment(headingLabel, Alignment.TOP_LEFT);
		this.addComponent(heading);

		List<GardenBed> gardenBeds;

		GardenBedDao daoGardenBed = new GardenBedDao();

		gardenBeds = daoGardenBed.getAll();

		HorizontalLayout bedHeadingHorizontal = new HorizontalLayout();
		this.addComponent(bedHeadingHorizontal);
		bedHeadingHorizontal.setWidth("100%");
		bedHeadingHorizontal.setMargin(new MarginInfo(false, true, false, false));
		bedHeadingHorizontal.setSpacing(false);

		Label bedHeadingLabel = new Label("Garden Bed");
		bedHeadingHorizontal.addComponent(bedHeadingLabel);
		bedHeadingHorizontal.setComponentAlignment(bedHeadingLabel, Alignment.MIDDLE_LEFT);
		bedHeadingHorizontal.setExpandRatio(bedHeadingLabel, 1.0f);
		bedHeadingLabel.setStyleName("i4p-label");
		Responsive.makeResponsive(bedHeadingLabel);
		bedHeadingLabel.setWidth(SWITCH_WIDTH, Unit.MM);

		Label lastWateredHeadingLabel = new Label("Last");
		bedHeadingHorizontal.addComponent(lastWateredHeadingLabel);
		lastWateredHeadingLabel.setStyleName("i4p-label");
		Responsive.makeResponsive(lastWateredHeadingLabel);
		lastWateredHeadingLabel.setWidth(FeatureRunLayout.LAST_WIDTH, Unit.MM);

		Label durationHeadingLabel = new Label("Duration");
		bedHeadingHorizontal.addComponent(durationHeadingLabel);
		durationHeadingLabel.setStyleName("i4p-label");
		Responsive.makeResponsive(durationHeadingLabel);
		durationHeadingLabel.setWidth(FeatureRunLayout.DURATION_WIDTH, Unit.MM);

		Panel scrollPanel = new Panel();
		this.addComponent(scrollPanel);
		this.setExpandRatio(scrollPanel, 1);
		scrollPanel.setSizeFull();

		VerticalLayout gardenBedLayout = new VerticalLayout();
		scrollPanel.setContent(gardenBedLayout);

		gardenBedLayout.setSizeFull();

		featureLines = new ArrayList<>();

		for (GardenBed gardenBed : gardenBeds)
		{
			EndPointBus.getInstance().addListener(gardenBed.getValve(), this);

			HorizontalLayout gardenBedHorizontal = new HorizontalLayout();
			gardenBedLayout.addComponent(gardenBedHorizontal);
			gardenBedHorizontal.setWidth("100%");

			Label nameLabel = new Label(gardenBed.getName());
			nameLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(nameLabel);

			gardenBedHorizontal.addComponent(nameLabel);
			gardenBedHorizontal.setExpandRatio(nameLabel, 1.0f);

			HorizontalLayout secondLinedHorizontal = new HorizontalLayout();
			gardenBedLayout.addComponent(secondLinedHorizontal);
			secondLinedHorizontal.setWidth("100%");

			Switch toggle = createOnOffSwitch(gardenBed);
			secondLinedHorizontal.addComponent(toggle);
			secondLinedHorizontal.setExpandRatio(toggle, 1.0f);
			// toggle.setWidth(SWITCH_WIDTH, Unit.MM);

			FeatureRunLayout line = new FeatureRunLayout(gardenBed);
			secondLinedHorizontal.addComponent(line);
			featureLines.add(line);

			Timer timer = TimerControl.getTimer(gardenBed);
			if (timer != null && timer.isTimerRunning())
				line.showTimer(timer);
		}

	}

	@Override
	public void afterViewChange(ViewChangeEvent event)
	{
		if (event.getOldView() == this)
			EndPointBus.getInstance().removeListener(this);
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event)
	{
		// we always let the view change through.
		return true;
	}

	private Switch createOnOffSwitch(GardenBed gardenBed)
	{
		Switch pinToggle = new Switch();
		pinToggle.setStyleName("i4p-switch");
		Responsive.makeResponsive(pinToggle);

		pinToggle.setValue(gardenBed.isOn());

		pinToggle.addValueChangeListener(e ->
			{
				if (e.isUserOriginated())
				{
					if (e.getValue() == true)
					{
						TimerDialog dialog = new TimerDialog("Watering Time", gardenBed, this);
						dialog.show(UI.getCurrent());
						// we leave the switch showing off until the user select and starts a timer.
						((Switch) e.getComponent()).setValue(false);
					}
					else
					{
						// If there is a timer running we need to cancel it.
						TimerControl.removeTimer(gardenBed);

						gardenBed.softOff();
						timerFinished(gardenBed);
					}
				}
			});

		switchMap.put(gardenBed.getValve(), pinToggle);

		return pinToggle;
	}

	/**
	 * Used by the MasterControl valve to notify us that it has started a drain timer.
	 */
	@Override
	public void timerStarted(EndPoint endPoint)
	{
		GardenFeature feature = findFeature(endPoint);

		if (feature != null)
		{
			// Update the feature line
			FeatureRunLayout line = findFeatureLine(feature);

			if (line != null)
				line.showTimer(TimerControl.getTimer(feature));
		}
	}

	@Override
	public void timerFinished(EndPoint endPoint)
	{
		GardenFeature feature = findFeature(endPoint);

		// Update the feature line
		FeatureRunLayout line = findFeatureLine(feature);

		if (line != null) line.timerFinished();
	}

	@Override
	public void timerStarted(GardenFeature feature, Duration duration)
	{
		// Update the feature line
		FeatureRunLayout line = findFeatureLine(feature);

		Timer timer = TimerControl.getTimer(feature);
		if (line != null) line.showTimer(timer);
	}

	@Override
	public void timerFinished(GardenFeature feature)
	{
		// Update the feature line
		FeatureRunLayout line = findFeatureLine(feature);

		if (line != null)
			line.timerFinished();
	}

	private GardenFeature findFeature(EndPoint endPoint)
	{
		GardenFeature found = null;

		for (FeatureRunLayout line : this.featureLines)
		{
			GardenFeature lineFeature = line.feature;

			if (lineFeature.getPrimaryEndPoint().equals(endPoint))
			{
				found = lineFeature;
				break;
			}
		}
		return found;
	}

	private FeatureRunLayout findFeatureLine(GardenFeature feature)
	{
		FeatureRunLayout found = null;

		for (FeatureRunLayout line : this.featureLines)
		{
			if (line.equals(feature))
			{
				found = line;
				break;
			}
		}
		return found;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void notifyHardOn(EndPoint gardenBed)
	{
		// when we get notified that the user started a timer we need to update the
		// switch so we show that the bed is on.
		Switch toggle = switchMap.get(gardenBed);
		toggle.setValue(true);
	}

	@Override
	public void notifyHardOff(EndPoint gardenBed)
	{
		// when we get notified that the timer finished need to update the
		// switch so we show that the bed is now off
		Switch toggle = switchMap.get(gardenBed);
		if (ui.isAttached())
			ui.access(() ->
				{
					toggle.setValue(false);
				});
	}

}
