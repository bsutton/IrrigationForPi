package au.org.noojee.irrigation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Element;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

@WebServlet(urlPatterns = "/*", name = "PiIrrigation", asyncSupported = true)
@VaadinServletConfiguration(ui = ControllerUI.class, productionMode = false)
public class PiIrrigationServlet extends VaadinServlet
{

	private static final long serialVersionUID = 1L;

	@Override
	protected void servletInitialized() throws ServletException
	{
		super.servletInitialized();

		getService().addSessionInitListener(new SessionInitListener()
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void sessionInit(SessionInitEvent event) throws ServiceException
			{
				event.getSession().addRequestHandler(new RequestHandler()
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public boolean handleRequest(VaadinSession session, VaadinRequest request,
							VaadinResponse response) throws IOException
					{

						String pathInfo = request.getPathInfo();
						InputStream in = null;

						// Logic for implementation of Progress Web App
						// ServiceWorker.js is the required service worker.
						// Read article at: https://vaadin.com/blog/progressive-web-apps-in-java
						// and 

						if (pathInfo.endsWith("ServiceWorker.js"))
						{
							response.setHeader("Service-Worker-Allowed", "/");
							response.setContentType("application/javascript");
							in = getClass().getResourceAsStream("/ServiceWorker.js");
						}

						if (in != null)
						{
							OutputStream out = response.getOutputStream();
							IOUtils.copy(in, out);
							in.close();
							out.close();

							return true;
						}
						else
						{

							return false;
						}
					}
				});

				event.getSession().addBootstrapListener(new BootstrapListener()
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void modifyBootstrapFragment(
							BootstrapFragmentResponse response)
					{
						// NOOP

					}

					@Override
					public void modifyBootstrapPage(BootstrapPageResponse response)
					{
						Element head = response.getDocument()
								.head();
 
						/** Add tags to make this a PWA app **/
						
						/** Give our app a title */
						head.prependElement("title").appendText("Pi-Gation");

						
						/** Icon for the home screen */
						head.prependElement("link")
						.attr("src", "/irrigation/VAADIN/themes/mytheme/images/pi-gation-192x192.png");

						
						/** Set the theme colour **/
						head.prependElement("meta")
								.attr("name", "theme-color")
								.attr("content", "#00b4f0");

						/** link to the manifest for the pwa **/
						head.prependElement("link")
								.attr("rel", "manifest")
								.attr("href", "VAADIN/manifest.json");

						/** Add the ProgressiveApp.js to the bottom of the page */
						Element body = response.getDocument().body();
						body.appendElement("script")
								.attr("type", "text/javascript")
								.attr("src", "./VAADIN/js/ProgressiveApp.js");
					}
				});
			}
		});
	}
}