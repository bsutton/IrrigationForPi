package au.org.noojee.irrigation.dao;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyEntityManagerUtil
{
	private static final Logger logger = LogManager.getLogger();
	private static EntityManagerFactory emf = null;
	
	
	public static void init()
	{
		init("IrrigationForPiPU");
	}
	
	public static void init(String persistenceUnitName)
	{
		try
		{
			if (emf == null)
				emf = Persistence.createEntityManagerFactory(persistenceUnitName);
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

	public static void databaseShutdown()
	{
		final String SHUTDOWN_CODE = "XJ015";
		System.out.println("SHUTTING DOWN");

		try
		{
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		}
		catch (SQLException e)
		{
			// Derby 10.9.1.0 shutdown raises a SQLException with code "XJ015"
			if (!SHUTDOWN_CODE.equals(e.getSQLState()))
			{
				e.printStackTrace();
			}
		}

	}

	
}
