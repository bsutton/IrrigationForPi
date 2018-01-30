package au.org.noojee.irrigation.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import au.org.noojee.irrigation.dao.EntityManagerProvider;
import au.org.noojee.irrigation.dao.EntityWorker;



/**
 * This filter injects an entity manager into any requests to the vaadin servlet.
 * @author bsutton
 *
 */

//Wire this filter in.
@WebFilter(filterName = "EntityManagerFilter", urlPatterns = {"/*"}, asyncSupported=true)

public class EntityManagerInjectorFilter implements Filter
{
	// private static transient Logger logger =
	org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
			final FilterChain filterChain) throws IOException, ServletException
	{
		try
		{
			EntityManagerProvider.setThreadLocalEntityManager(new EntityWorker<Void>()
			{

				@Override
				public Void exec() throws Exception
				{
					filterChain.doFilter(servletRequest, servletResponse);
					return null;
				}
			});
		}
		catch (Exception e1)
		{
			logger.error(e1, e1);
		}
	}

	@Override
	public void destroy()
	{
	}
}
