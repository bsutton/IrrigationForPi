package au.org.noojee.irrigation.views;

import java.time.Duration;
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

import au.org.noojee.irrigation.dao.LightingDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.entities.Lighting;
import au.org.noojee.irrigation.types.EndPointBus;

public class LightingView extends VerticalLayout implements SmartView, EndPointChangeListener, ViewChangeListener, TimerNotification
{

	private static final int SWITCH_WIDTH = 35;
	private static final int LAST_WIDTH = 28;
	private static final int DURATION_WIDTH = 22;
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Lighting";

	private Map<EndPoint, Switch> switchMap = new HashMap<>();
	private boolean supressChangeListener = false;
	private UI ui;

	public LightingView()
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
		Label headingLabel = new Label("Lighting");
		headingLabel.setStyleName("i4p-heading");
		Responsive.makeResponsive(headingLabel);
		heading.addComponent(headingLabel);
		heading.setComponentAlignment(headingLabel, Alignment.TOP_LEFT);
		this.addComponent(heading);

		List<Lighting> lightingList;

		LightingDao daoLighting = new LightingDao();

		lightingList = daoLighting.getAll();

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

		VerticalLayout lightingLayout = new VerticalLayout();
		scrollPanel.setContent(lightingLayout);

		lightingLayout.setSizeFull();

		for (Lighting lighting : lightingList)
		{
			EndPointBus.getInstance().addListener(lighting.getLightSwitch(), this);

			HorizontalLayout lightingHorizontal = new HorizontalLayout();
			lightingLayout.addComponent(lightingHorizontal);
			lightingHorizontal.setWidth("100%");

			Label nameLabel = new Label(lighting.getName());
			nameLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(nameLabel);

			lightingHorizontal.addComponent(nameLabel);
			lightingHorizontal.setExpandRatio(nameLabel, 1.0f);

			HorizontalLayout secondLinedHorizontal = new HorizontalLayout();
			lightingLayout.addComponent(secondLinedHorizontal);
			secondLinedHorizontal.setWidth("100%");

			Switch toggle = createOnOffSwitch(lighting);
			secondLinedHorizontal.addComponent(toggle);
			secondLinedHorizontal.setExpandRatio(toggle, 1.0f);
			// toggle.setWidth(SWITCH_WIDTH, Unit.MM);

			switchMap.put(lighting.getLightSwitch(), toggle);

			// History history = lighting.getLastWatering();
			// if (history != null)
			// {
			// Label lastWateredLabel = new Label(Formatters.format(history.getStartDate().toLocalDate()));
			// secondLinedHorizontal.addComponent(lastWateredLabel);
			// lastWateredLabel.setWidth(LAST_WIDTH, Unit.MM);
			// lastWateredLabel.setStyleName("i4p-label");
			// Responsive.makeResponsive(lastWateredLabel);
			//
			// Label durationLabel = new Label(Formatters.format(history.getDuration()));
			// secondLinedHorizontal.addComponent(durationLabel);
			// durationLabel.setWidth(DURATION_WIDTH, Unit.MM);
			// durationLabel.setStyleName("i4p-label");
			// Responsive.makeResponsive(durationLabel);
			//
			// }
			//
		}

	}

	private Switch createOnOffSwitch(Lighting lighting)
	{
		Switch pinToggle = new Switch();
		pinToggle.setStyleName("i4p-switch");
		Responsive.makeResponsive(pinToggle);

		pinToggle.setValue(lighting.isOn());

		pinToggle.addValueChangeListener(e ->
			{
				if (!this.supressChangeListener)
				{
					if (e.getValue() == true)
					{
						TimerDialog dialog = new TimerDialog("Lighting Time", lighting, this);
						dialog.show(UI.getCurrent());
						// we leave the switch showing off until the user selects and starts a timer.
						this.supressChangeListener = true;
						((Switch) e.getComponent()).setValue(false);
						this.supressChangeListener = false;
					}
					else
						lighting.softOff();
				}
			});

		return pinToggle;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void notifyHardOn(EndPoint lightSwitch)
	{
		this.supressChangeListener = true;
		// when we get notified that the user started a timer we need to update the
		// switch so we show that the bed is on.
		Switch toggle = switchMap.get(lightSwitch);
		toggle.setValue(true);

		this.supressChangeListener = false;

	}

	@Override
	public void notifyHardOff(EndPoint lightSwitch)
	{

		this.supressChangeListener = true;
		// when we get notified that the timer finished need to update the
		// switch so we show that the bed is now off
		Switch toggle = switchMap.get(lightSwitch);
		if (ui.isAttached())
			ui.access(() ->
				{
					toggle.setValue(false);
				});
		this.supressChangeListener = false;
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
		// We always let the view change.
		return true;
	}

	@Override
	public void timerStarted(GardenFeature feature, Duration duration)
	{
		// TODO Auto-generated method stub
		
	}

}
