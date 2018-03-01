package au.org.noojee.irrigation.servlets;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.util.Strings;

/**
 * This filter redirects all traffic to https (port 443).
 * 
 * @author bsutton
 */

// Wire this filter in.
@WebFilter(filterName = "HTTPSRedirect", urlPatterns = { "/*" }, asyncSupported = true)

public class HTTPSRedirectFilter implements Filter
{
	// private static transient Logger logger =
	org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger();
	
	private boolean redirect = true;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		// check if we are in a debug environment. If so don't do the redirect.
		if (Strings.isNotBlank(System.getenv("allow-port-80")))
			this.redirect = false;
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
			final FilterChain filterChain) throws IOException, ServletException
	{
		
		if (redirect && servletRequest.getScheme() =="http")
		{
			HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
			
			httpResponse.sendRedirect(new URL("https://" + servletRequest.getServerName()).toString());
		}
		else
			filterChain.doFilter(servletRequest, servletResponse);

	}

	@Override
	public void destroy()
	{
	}
}
