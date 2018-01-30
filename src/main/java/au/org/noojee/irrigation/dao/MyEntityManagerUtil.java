package au.org.noojee.irrigation.dao;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
		init("Production");
	}

	public static void initTest()
	{
		init("Test");
	}

	public static EntityManagerFactory getEntityManagerFactory()
	{
		if (emf == null)
			throw new IllegalStateException("Call MyEntityManagerUtil.init() first");
		return MyEntityManagerUtil.emf;
	}

	public static void init(String persistenceUnitName)
	{
		try
		{
			if (emf == null)
			{
				/*
				 * <property name="javax.persistence.jdbc.url"
				 * value="jdbc:derby:/home/pi/irrigationDb/IrrigationForPi;create=true" /> <property
				 * name="javax.persistence.jdbc.password" value="a password goes here" /> <property
				 * name="javax.persistence.jdbc.user" value="irrigation4pi" />
				 */

				// Default path.
				File rootPath = new File("/home/pi/irrigationDb");

				// If we are running under snap then use the snap data path
				String snapPath = System.getenv("SNAP_DATA");
				if (snapPath != null)
					rootPath = new File(snapPath);

				File dbPath = new File(rootPath, persistenceUnitName);

				String username = System.getenv("pi_gation_db_username");
				String password = System.getenv("pi_gation_db_password");

				if (username == null)
					throw new RuntimeException(
							"You must create an environment variable pi_gation_db_username which contains the db username");

				if (password == null)
					throw new RuntimeException(
							"You must create an environment variable pi_gation_db_password which contains the db password");

				String jdbcURL = "jdbc:derby:" + dbPath.getAbsolutePath() + ";create=true";

				Map<String, String> properties = new HashMap<>();

				properties.put("javax.persistence.jdbc.url", jdbcURL);
				properties.put("javax.persistence.jdbc.password", password);
				properties.put("javax.persistence.jdbc.user", username);

				emf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
			}
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

	//
	// static EntityManager createEntityManager()
	// {
	// if (emf == null)
	// throw new IllegalStateException("Call EntityManagerUtil.init() on program startup");
	// return emf.createEntityManager();
	// }
	//
	// public static EntityManager getEntityManager()
	// {
	//
	// return emf.createEntityManager();
	// }
	//
	public static void databaseShutdown()
	{
		final String SHUTDOWN_CODE = "XJ015";
		logger.info("Shutting Down Derby Database connection.");

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
