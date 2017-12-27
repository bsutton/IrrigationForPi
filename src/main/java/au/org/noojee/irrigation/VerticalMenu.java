package au.org.noojee.irrigation;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class VerticalMenu extends VerticalLayout
{
	private static final long serialVersionUID = 1L;

	VerticalMenu()
	{
		this.setMargin(new MarginInfo(true, false));
		
		//this.setHeight("100%");
		Button irrigation = new Button("", VaadinIcons.CLOUD);
		this.addComponent(irrigation);
		irrigation.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(IrrigationView.NAME);
		});

		
		Button lighting = new Button("", VaadinIcons.LIGHTBULB);
		this.addComponent(lighting);
		lighting.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(LightingView.NAME);
		});

	
		
		Button schedule = new Button("", VaadinIcons.CLOCK);
		this.addComponent(schedule);
		schedule.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(ScheduleView.NAME);
		});

	
		Button configuration = new Button("", VaadinIcons.COG);
		this.addComponent(configuration);
		configuration.addClickListener(l -> {
			UI.getCurrent().getNavigator().navigateTo(ConfigurationView.NAME);
		});

		
	}
	
}
