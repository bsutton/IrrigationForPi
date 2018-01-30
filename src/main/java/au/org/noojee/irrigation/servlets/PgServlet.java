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

// <servlet>
// <servlet-name>Vaadin Application Servlet</servlet-name>
// <servlet-class>com.vaadin.server.VaadinServlet</servlet-class>
// <init-param>
// <description>Vaadin UI to display</description>
// <param-name>UI</param-name>
// <param-value>au.org.scoutmaster.application.NavigatorUI</param-value>
// </init-param>
// <init-param>
// <param-name>org.atmosphere.cpr.AtmosphereInterceptor</param-name>
// <!-- comma-separated list of fully-qualified class names -->
// <param-value>au.com.vaadinutils.servlet.AtmosphereFilter</param-value>
// </init-param>
// <async-supported>true</async-supported>

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