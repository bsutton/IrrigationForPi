package au.org.noojee.irrigation;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.views.ConfigurationView;
import au.org.noojee.irrigation.views.IrrigationView;
import au.org.noojee.irrigation.views.LightingView;
import au.org.noojee.irrigation.views.ScheduleView;
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

	@Override
	protected void init(VaadinRequest vaadinRequest)
	{
		final HorizontalLayout layout = new HorizontalLayout();

		layout.addComponent(new VerticalMenu());
		VerticalLayout view = new VerticalLayout();
		layout.addComponent(view);

		nav = new Navigator(this, view);
		nav.addView("", new IrrigationView());
		nav.addView(IrrigationView.NAME, new IrrigationView());
		nav.addView(LightingView.NAME, new LightingView());
		nav.addView(ScheduleView.NAME, new ScheduleView());
		nav.addView(ConfigurationView.NAME, new ConfigurationView());
		nav.addView(TouchConfigurationView.NAME, new TouchConfigurationView());
		

	}

	@WebServlet(urlPatterns = "/*", name = "PiIrrigation", asyncSupported = true)
	@VaadinServletConfiguration(ui = ControllerUI.class, productionMode = false)
	public static class PiIrrigationServlet extends VaadinServlet
	{

		private static final long serialVersionUID = 1L;

	}
}
