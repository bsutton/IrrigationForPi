package au.org.noojee.irrigation;

import java.util.HashMap;

import com.github.appreciated.app.layout.behaviour.AppLayoutComponent;
import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayout;
import com.github.appreciated.app.layout.builder.design.AppBarDesign;
import com.github.appreciated.app.layout.builder.elements.builders.SubmenuBuilder;
import com.github.appreciated.app.layout.component.MenuHeader;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.UserDao;
import au.org.noojee.irrigation.views.EndPointConfigurationView;
import au.org.noojee.irrigation.views.ForgottenPasswordView;
import au.org.noojee.irrigation.views.GardenBedConfigurationView;
import au.org.noojee.irrigation.views.GardenBedView;
import au.org.noojee.irrigation.views.HistoryView;
import au.org.noojee.irrigation.views.FirstRunView;
import au.org.noojee.irrigation.views.LightingView;
import au.org.noojee.irrigation.views.LoginView;
import au.org.noojee.irrigation.views.OverviewView;
import au.org.noojee.irrigation.views.ResetPasswordView;
import au.org.noojee.irrigation.views.ScheduleView;
import au.org.noojee.irrigation.views.SmartView;
import au.org.noojee.irrigation.views.UserView;
import au.org.noojee.irrigation.views.editors.EndPointEditorView;
import au.org.noojee.irrigation.views.editors.GardenBedEditorView;
import au.org.noojee.irrigation.views.editors.UserEditorView;

/**
 * This UI is the application entry point. A UI may either represent a browser window (or tab) or some part of an HTML
 * page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be overridden to add component
 * to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Viewport("width=device-width, initial-scale=1.0")
@PushStateNavigation
@Push
@Widgetset("au.org.noojee.irrigation.widgets.PiGationWidgetset")
public class ControllerUI extends UI  implements ViewChangeListener
{

	private static final long serialVersionUID = 1L;
	Navigator nav;
	private HashMap<String, SmartView> views = new HashMap<>();
	
	// login
	LoginView loginView = new LoginView();


	
	@Override
	protected void init(VaadinRequest vaadinRequest)
	{
		if (PiGationConfig.SELF.isConfigured() && UserDao.isLoggedIn())
			loadLayout();
		else
		{
			setContent(loginView);
			PublicNavigator navigator = new PublicNavigator(this);
			setNavigator(navigator);
			navigator.addViewChangeListener(this);
		}
	}

	public void loadLayout()
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
		UserView userView = new UserView();

		EndPointConfigurationView endPointConfigurationView = new EndPointConfigurationView();
		GardenBedConfigurationView gardenBedConfigurationView = new GardenBedConfigurationView();
		// Non-menu views:
		EndPointEditorView endPointEditor = new EndPointEditorView();
		GardenBedEditorView gardenBedEditor = new GardenBedEditorView();
		UserEditorView userEditor = new UserEditorView();

		final Button refreshMenu = new Button(VaadinIcons.ELLIPSIS_DOTS_V);
		refreshMenu.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		ContextMenu menu = new ContextMenu(refreshMenu, true);
		menu.addItem("Refresh", null, (m) -> Page.getCurrent().reload());
		refreshMenu.addClickListener(l -> menu.open(l.getClientX(), l.getClientY()));

		AppLayoutComponent layout = AppLayout.getDefaultBuilder(Behaviour.LEFT_RESPONSIVE)
				.withTitle("Pi-gation")
				.withDefaultNavigationView(overviewView)
				.withCloseSubmenusOnNavigate(true)
				.addToAppBar(refreshMenu)
				.withDesign(AppBarDesign.MATERIAL)
				.add(new MenuHeader("Irrigation & Lighting", new ThemeResource("icons/pi-gation.png")))
				.add(OverviewView.NAME, VaadinIcons.HOME, overviewView)
				.add(GardenBedView.LABEL, GardenBedView.NAME, VaadinIcons.CLOUD, null, gardenBedView)
				.add(LightingView.NAME, VaadinIcons.LIGHTBULB, lightingView)
				.add(ScheduleView.NAME, VaadinIcons.CLOCK, scheduleView)
				.add(HistoryView.NAME, VaadinIcons.TIME_BACKWARD, historyView)
				.add(SubmenuBuilder.get("Configuration", VaadinIcons.COG)
						.add(EndPointConfigurationView.NAME, VaadinIcons.CONNECT, endPointConfigurationView)
						.add(GardenBedConfigurationView.LABEL,
								GardenBedConfigurationView.NAME, VaadinIcons.DROP, gardenBedConfigurationView)
						.add(UserView.NAME, VaadinIcons.USER, userView)
						.build())
				.build();

		getNavigator().addView(GardenBedConfigurationView.NAME, gardenBedConfigurationView);
		getNavigator().addViewChangeListener(gardenBedView);
		getNavigator().addViewChangeListener(lightingView);

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
		getNavigator().addView(EndPointEditorView.NAME, endPointEditor);
		addView(gardenBedEditor);
		getNavigator().addView(GardenBedEditorView.NAME, gardenBedEditor);
		addView(userEditor);
		getNavigator().addView(UserEditorView.NAME, userEditor);

		setContent(layout);

	}
	
	private void logout()
	{
		UserDao.logout();

		this.setNavigator(new PublicNavigator(this));
		this.getNavigator().navigateTo(LoginView.NAME);
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
	
	@Override
	public boolean beforeViewChange(ViewChangeEvent event)
	{
		boolean allow = false;

		// Always allow access to login page.
		if (isPublicPage(event))
		{
			UserDao.logout();
			setContent(null);
			allow = true;
		}
		else if (UserDao.isLoggedIn())
			allow = true; // the user is logged in.

		if (!allow)
			this.getNavigator().navigateTo(LoginView.NAME);
		return allow;

	}
	
	private boolean isPublicPage(ViewChangeEvent event)
	{
		boolean isPublic = false;

		// add more public views here.
		isPublic |= event.getNewView() instanceof LoginView;
		isPublic |= event.getNewView() instanceof ForgottenPasswordView;
		isPublic |= event.getNewView() instanceof ResetPasswordView;
		
		if (!PiGationConfig.SELF.isConfigured())
			isPublic |= event.getNewView() instanceof FirstRunView;

		return isPublic;
	}



}
