package au.org.noojee.irrigation.servlets;

import javax.persistence.EntityManager;

import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereResource;

import au.org.noojee.irrigation.dao.EntityManagerProvider;
import au.org.noojee.irrigation.dao.Transaction;

/**
 * Designed to inject the EntityManager into requests that arrive via websockets (Vaadin Push) as these
 * do not go through the standard servlet filter mechanism.
 * 
 * This class is installed by adding a parameter to the VaadinServlet using an annotation
 * 
 *  @formatter:off 
 *  
 *  e.g.
 *  @WebServlet(urlPatterns = "/*", name = "PiIrrigation", asyncSupported = true, 
 *  initParams =
 *  {
 *  		@WebInitParam(name = "org.atmosphere.cpr.AtmosphereInterceptor", value = "au.org.noojee.irrigation.servlets.AtmosphereFilter")
 *  })
 *  
 *  @formatter:on
 * 
 */

public class AtmosphereFilter extends AtmosphereInterceptorAdapter
{
	Transaction t;
	private EntityManager em;
	private boolean releaseEm;

	public Action inspect(AtmosphereResource r)
	{
		// do pre-request stuff
		// First check that there isn't alrady an activity em. We can sometimes
		// be changed from within a servlet request because atmosphere does weird shit.

		em = EntityManagerProvider.getEntityManager();

		if (em == null)
		{
			em = EntityManagerProvider.createEntityManager();
			t = new Transaction(em);

			// Create and set the entity manager
			EntityManagerProvider.setCurrentEntityManager(em);
			releaseEm = true;
		}
		else
			releaseEm = false;

		return super.inspect(r);
	}

	// do post-request stuff (Vaadin request handling is done at this point)
	public void postInspect(AtmosphereResource r)
	{
		try
		{
			if (releaseEm)
				t.commit();

		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if (releaseEm)
			{
				t.close();
				em.close();

				// Reset the entity manager as we get a new one everytime we inspect is called.
				EntityManagerProvider.setCurrentEntityManager(null);
			}
		}

	}
}
