package au.org.noojee.irrigation;

import java.util.HashMap;

import com.github.appreciated.app.layout.behaviour.AppLayout;
import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder.Position;
import com.github.appreciated.app.layout.builder.design.AppBarDesign;
import com.github.appreciated.app.layout.builder.elements.SubmenuBuilder;
import com.github.appreciated.app.layout.builder.entities.DefaultBadgeHolder;
import com.github.appreciated.app.layout.component.MenuHeader;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import au.org.noojee.irrigation.views.EndPointConfigurationView;
import au.org.noojee.irrigation.views.GardenBedConfigurationView;
import au.org.noojee.irrigation.views.GardenBedView;
import au.org.noojee.irrigation.views.HistoryView;
import au.org.noojee.irrigation.views.LightingView;
import au.org.noojee.irrigation.views.OverviewView;
import au.org.noojee.irrigation.views.ScheduleView;
import au.org.noojee.irrigation.views.SmartView;
import au.org.noojee.irrigation.views.editors.EndPointEditorView;
import au.org.noojee.irrigation.views.editors.GardenBedEditorView;

/**
 * This UI is the application entry point. A UI may either represent a browser window (or tab) or some part of an HTML
 * page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be overridden to add component
 * to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
// Turns our web app in to a Progress Web App
// Read article at: https://vaadin.com/blog/progressive-web-apps-in-java
// @JavaScript("vaadin://js/app.js")

@Viewport("width=device-width, initial-scale=1.0")
@PushStateNavigation
public class ControllerUI extends UI
{

	private static final long serialVersionUID = 1L;
	Navigator nav;
	private HashMap<String, SmartView> views = new HashMap<>();

	@Override
	protected void init(VaadinRequest vaadinRequest)
	{
		// DefaultBadgeHolder holder = new DefaultBadgeHolder(); // I can indicate updates in another "navigatable"
		// element
		// holder.increase();
		//

		OverviewView overviewView = new OverviewView(); 
		GardenBedView gardenBedView = new GardenBedView(); 
		LightingView lightingView = new LightingView();
		ScheduleView scheduleView = new ScheduleView();
		HistoryView historyView = new HistoryView();
		EndPointConfigurationView endPointConfigurationView = new EndPointConfigurationView();
		GardenBedConfigurationView gardenBedConfigurationView = new GardenBedConfigurationView();
		// Non-menu views:
		EndPointEditorView endPointEditor = new EndPointEditorView();
		GardenBedEditorView gardenBedEditor = new GardenBedEditorView();

		AppLayout layout = AppLayoutBuilder.get(Behaviour.LEFT_RESPONSIVE)
				.withTitle("Pi-gation")
				.withDefaultNavigationView(overviewView)
				.withDesign(AppBarDesign.MATERIAL)
				.add(new MenuHeader("Irrigation & Lighting", new ThemeResource("icons/pi-gation.png")), Position.HEADER)
				.add(OverviewView.NAME, VaadinIcons.HOME, overviewView)
				.add(GardenBedView.LABEL, GardenBedView.NAME, VaadinIcons.CLOUD, null, gardenBedView, Position.DEFAULT)
				.add(LightingView.NAME, VaadinIcons.LIGHTBULB, lightingView)
				.add(ScheduleView.NAME, VaadinIcons.CLOCK, scheduleView)
				.add(HistoryView.NAME, VaadinIcons.TIME_BACKWARD, historyView)
				.add(SubmenuBuilder.get("Configuration", VaadinIcons.COG)
						.add(EndPointConfigurationView.NAME, VaadinIcons.CONNECT, endPointConfigurationView)
						.add(GardenBedConfigurationView.LABEL, 
								GardenBedConfigurationView.NAME, VaadinIcons.CLOUD, gardenBedConfigurationView)
						.build(), Position.FOOTER)
				.build();

		UI.getCurrent().getNavigator().addView(GardenBedConfigurationView.NAME, gardenBedConfigurationView);
		/*
		 * .addClickable("Click Me", VaadinIcons.QUESTION, clickEvent -> { }) .add("Preferences", VaadinIcons.COG,
		 * View6.class, FOOTER)
		 */
		layout.setSizeFull();

		addView(gardenBedView);
		addView(lightingView);
		addView(scheduleView);
		addView(endPointConfigurationView);
		addView(gardenBedConfigurationView);
		// Non-menu views:
		addView(endPointEditor);
		UI.getCurrent().getNavigator().addView(EndPointEditorView.NAME, endPointEditor);
		addView(gardenBedEditor);
		UI.getCurrent().getNavigator().addView(GardenBedEditorView.NAME, gardenBedEditor);

		setContent(layout);

	}

	void addView(SmartView view)
	{
		views.put(view.getName(), view);

		// if (defaultView)
		// nav.addView("", view);
		//
		// nav.addView(view.getName(), view);
		//
	}

	public SmartView getView(String name)
	{
		return views.get(name);
	}

}
