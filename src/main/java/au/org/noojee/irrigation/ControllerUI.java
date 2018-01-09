package au.org.noojee.irrigation;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.entities.EntityManagerUtil;
import au.org.noojee.irrigation.views.ConfigurationView;
import au.org.noojee.irrigation.views.DefinePinView;
import au.org.noojee.irrigation.views.IrrigationView;
import au.org.noojee.irrigation.views.LightingView;
import au.org.noojee.irrigation.views.ScheduleView;
import au.org.noojee.irrigation.views.SmartView;
import au.org.noojee.irrigation.views.TouchConfigurationView;

/**
 * This UI is the application entry point. A UI may either represent a browser window (or tab) or some part of an HTML
 * page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be overridden to add component
 * to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class ControllerUI extends UI
{

	private static final long serialVersionUID = 1L;
	Navigator nav;
	private HashMap<String, SmartView> views = new HashMap<>();

	@Override
	protected void init(VaadinRequest vaadinRequest)
	{
		final HorizontalLayout page = new HorizontalLayout();
		this.setContent(page);

		page.setSizeFull();
		page.setMargin(false);

		page.addComponent(new VerticalMenu());
		VerticalLayout viewLayout = new VerticalLayout();
		viewLayout.setId("ViewContainer");
		viewLayout.setSizeFull();
		viewLayout.setMargin(false);
		page.addComponent(viewLayout);
		
		page.setExpandRatio(viewLayout, 1);

		nav = new Navigator(this, viewLayout);

		addView(new IrrigationView(), true);
		addView(new LightingView(), false);
		addView(new ScheduleView(), false);
		addView(new ConfigurationView(), false);
		addView(new TouchConfigurationView(), false);
		// 	Non-menu views:
		addView(new DefinePinView(), false);

	}
	
	void addView(SmartView view, boolean defaultView)
	{
		views.put(view.getName(), view);
		
		if (defaultView)
			nav.addView("", view);
		
		nav.addView(view.getName(), view);

	}

	@WebServlet(urlPatterns = "/*", name = "PiIrrigation", asyncSupported = true)
	@VaadinServletConfiguration(ui = ControllerUI.class, productionMode = false)
	public static class PiIrrigationServlet extends VaadinServlet
	{

		private static final long serialVersionUID = 1L;


	}

	public  SmartView getView(String name)
	{
		return views.get(name);
	}

}
