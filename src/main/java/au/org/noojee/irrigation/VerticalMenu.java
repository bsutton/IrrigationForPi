package au.org.noojee.irrigation;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.views.EndPointConfigurationView;
import au.org.noojee.irrigation.views.GardenBedConfigurationView;
import au.org.noojee.irrigation.views.LightingView;
import au.org.noojee.irrigation.views.ScheduleView;

public class VerticalMenu extends VerticalLayout
{
	private static final long serialVersionUID = 1L;

	VerticalMenu()
	{
		this.setMargin(new MarginInfo(true, false));
		Button irrigation = new Button("", VaadinIcons.CLOUD);
		irrigation.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(irrigation);
		irrigation.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(GardenBedConfigurationView.NAME);
		});

		
		Button lighting = new Button("", VaadinIcons.LIGHTBULB);
		lighting.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(lighting);
		lighting.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(LightingView.NAME);
		});

	
		
		Button schedule = new Button("", VaadinIcons.CLOCK);
		schedule.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(schedule);
		schedule.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(ScheduleView.NAME);
		});

	
		Button configuration = new Button("", VaadinIcons.COG);
		configuration.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(configuration);
		configuration.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(EndPointConfigurationView.NAME);
		});
		
		Button touchConfiguration = new Button("", VaadinIcons.COG);
		touchConfiguration.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(touchConfiguration);
		touchConfiguration.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(EndPointConfigurationView.NAME);
		});

		// Use an arbitary button to control the width of the menu bar.
		this.setWidth(schedule.getWidth(), schedule.getWidthUnits());

		
	}
	
}
