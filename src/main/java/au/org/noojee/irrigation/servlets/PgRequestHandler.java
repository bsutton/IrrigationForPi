package au.org.noojee.irrigation.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

final class PgRequestHandler implements RequestHandler
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
}