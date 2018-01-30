package au.org.noojee.irrigation.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import au.org.noojee.irrigation.ControllerUI;

@WebServlet(urlPatterns = "/*", name = "PiIrrigation", asyncSupported = true, initParams =
{
		@WebInitParam(name = "org.atmosphere.cpr.AtmosphereInterceptor", value = "au.org.noojee.irrigation.servlets.AtmosphereFilter")
})
@VaadinServletConfiguration(ui = ControllerUI.class, productionMode = false)
public class PgServlet extends VaadinServlet
{

	private static final long serialVersionUID = 1L;

	@Override
	protected void servletInitialized() throws ServletException
	{
		super.servletInitialized();

		getService().addSessionInitListener(new PgSessionListener());
	}
}