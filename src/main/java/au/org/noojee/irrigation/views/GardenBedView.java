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

import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.entities.History;
import au.org.noojee.irrigation.types.EndPointBus;
import au.org.noojee.irrigation.util.Formatters;

public class GardenBedView extends VerticalLayout
		implements SmartView, EndPointChangeListener, ViewChangeListener, TimerNotification
{

	private static final int SWITCH_WIDTH = 35;
	private static final int LAST_WIDTH = 28;
	private static final int DURATION_WIDTH = 22;
	private static final long serialVersionUID = 1L;
	public static final String NAME = "GardenBeds";
	public static final String LABEL = "Garden Beds";

	private Map<EndPoint, Switch> switchMap = new HashMap<>();
	private boolean supressChangeListener = false;
	private UI ui;

	List<FeatureLine> featureLines;

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
		lastWateredHeadingLabel.setWidth(LAST_WIDTH, Unit.MM);

		Label durationHeadingLabel = new Label("Duration");
		bedHeadingHorizontal.addComponent(durationHeadingLabel);
		durationHeadingLabel.setStyleName("i4p-label");
		Responsive.makeResponsive(durationHeadingLabel);
		durationHeadingLabel.setWidth(DURATION_WIDTH, Unit.MM);

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

			FeatureLine line = new FeatureLine(gardenBed, secondLinedHorizontal);
			featureLines.add(line);

			Switch toggle = createOnOffSwitch(gardenBed, line);
			secondLinedHorizontal.addComponent(toggle);
			secondLinedHorizontal.setExpandRatio(toggle, 1.0f);
			// toggle.setWidth(SWITCH_WIDTH, Unit.MM);

			History history = gardenBed.getLastWatering();

			Label lastWateredLabel;
			Label durationLabel;

			if (history != null)
			{

				lastWateredLabel = new Label(Formatters.format(history.getStart().toLocalDate()));
				durationLabel = new Label(Formatters.format(history.getDuration()));
			}
			else
			{
				lastWateredLabel = new Label();
				durationLabel = new Label();
			}

			line.setLabel(lastWateredLabel);
			line.setDurationLabel(durationLabel);

			secondLinedHorizontal.addComponent(lastWateredLabel);
			lastWateredLabel.setWidth(LAST_WIDTH, Unit.MM);
			lastWateredLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(lastWateredLabel);

			secondLinedHorizontal.addComponent(durationLabel);
			durationLabel.setWidth(DURATION_WIDTH, Unit.MM);
			durationLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(durationLabel);

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

	private Switch createOnOffSwitch(GardenBed gardenBed, FeatureLine line)
	{
		Switch pinToggle = new Switch();
		pinToggle.setStyleName("i4p-switch");
		Responsive.makeResponsive(pinToggle);

		pinToggle.setValue(gardenBed.isOn());

		pinToggle.addValueChangeListener(e ->
			{
				if (!this.supressChangeListener)
				{
					if (e.getValue() == true)
					{
						TimerDialog dialog = new TimerDialog("Watering Time", gardenBed, this);
						dialog.show(UI.getCurrent());
						// we leave the switch showing off until the user select and starts a timer.
						this.supressChangeListener = true;
						((Switch) e.getComponent()).setValue(false);
						this.supressChangeListener = false;
					}
					else
					{
						gardenBed.softOff();
						timerFinished(gardenBed);
					}
				}
			});

		switchMap.put(gardenBed.getValve(), pinToggle);

		return pinToggle;
	}

	@Override
	public void timerStarted(GardenFeature feature, Duration duration)
	{
		// Update the feature line
		FeatureLine line = findFeatureLine(feature);

		line.showTimer("Running", duration);
	}

	@Override
	public void timerFinished(GardenFeature feature)
	{
		// Update the feature line
		FeatureLine line = findFeatureLine(feature);

		if (line != null)
			line.timerFinished();
	}

	private FeatureLine findFeatureLine(GardenFeature feature)
	{
		FeatureLine found = null;

		for (FeatureLine line : this.featureLines)
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
		this.supressChangeListener = true;

		// when we get notified that the user started a timer we need to update the
		// switch so we show that the bed is on.
		Switch toggle = switchMap.get(gardenBed);
		toggle.setValue(true);

		this.supressChangeListener = false;

	}

	@Override
	public void notifyHardOff(EndPoint gardenBed)
	{

		this.supressChangeListener = true;
		// when we get notified that the timer finished need to update the
		// switch so we show that the bed is now off
		Switch toggle = switchMap.get(gardenBed);
		if (ui.isAttached())
			ui.access(() ->
				{
					toggle.setValue(false);
				});
		this.supressChangeListener = false;
	}

}
