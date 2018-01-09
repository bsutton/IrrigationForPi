package au.org.noojee.irrigation.entities;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityManagerUtil
{
	private static final Logger logger = LogManager.getLogger();
	private static EntityManagerFactory emf = null;
	
	
	public static void init()
	{
		try
		{
			if (emf == null)
				emf = Persistence.createEntityManagerFactory("IrrigationForPiPU");
			else
			{
				logger.error("Someone is trying to initialise EntityManagerUtil a second time." 
						+ Arrays.toString(Thread.currentThread().getStackTrace()));
			}
			
		}
		catch (Throwable e)
		{
			logger.error("Initial Session factory creation failed: " + e);
			throw e;
		}
	}
	
	static EntityManager createEntityManager()
	{
		if (emf == null)
			throw new IllegalStateException("Call EntityManagerUtil.init() on program startup");
		return emf.createEntityManager();
	}
	
	public static EntityManager getEntityManager()
	{
		
		return emf.createEntityManager();
		
		// should really use a new entity manager per servlet request.
		// rather than creating one every time we hit the db. 
		// But for the moment this is easy.
		// If we get aspects working we can probably work out what request we are in.
		/*
		EntityManager em = EntityAspect.getThreadEntityManager();
		
		logger.error("Active Em=" + em.toString() + " from thread: " + Thread.currentThread().getId());
		logger.error("Owning factory is: " + em.getEntityManagerFactory().toString());
		return em;
		*/
	}
}
