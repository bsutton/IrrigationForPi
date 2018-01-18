package au.org.noojee.irrigation;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.EntityManagerUtil;

/**
 * @author Brett Sutton
 */
@WebListener
public class WebAppListener implements ServletContextListener
{
	Logger logger = LogManager.getLogger();

	public WebAppListener()
	{

	}

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		logger.info("Irrigation Manager is starting. ");

		System.out.println("PI PLATFORM: " + System.getenv("PI4J_PLATFORM"));
		System.out.println("Simulated PLATFORM - simulated: " + System.getenv("SimulatedPlatform"));

		EntityManagerUtil.init();

		provisionPins();

	}

	public static void provisionPins()
	{
		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		EndPointDao daoPin = new EndPointDao();
		List<au.org.noojee.irrigation.entities.EndPoint> pins = daoPin.getAll();

		// Set default states for pins.
		for (Pin pin : RaspiPin.allPins())
		{
			PinState offState = PinState.HIGH;
			au.org.noojee.irrigation.entities.EndPoint configuredPin = getConfiguredPin(pin, pins);
			if (configuredPin != null)
				offState = configuredPin.getPinActiviationType().getOffState();

			GpioPinDigitalOutput digitPin = gpio.provisionDigitalOutputPin(pin, offState);
			digitPin.setShutdownOptions(true, PinState.LOW);
		}
	}

	static private EndPoint getConfiguredPin(Pin pin,
			List<au.org.noojee.irrigation.entities.EndPoint> pins)
	{
		au.org.noojee.irrigation.entities.EndPoint configuredPin = null;

		for (au.org.noojee.irrigation.entities.EndPoint checkPin : pins)
		{
			if (checkPin.getPinNo() == pin.getAddress())
			{
				configuredPin = checkPin;
				break;
			}
		}
		return configuredPin;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		logger.info("Irrigation Manager is shutting down.");
		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and
		// scheduled tasks)
		final GpioController gpio = GpioFactory.getInstance();

		gpio.shutdown();

		databaseShutdown();

	}

	private void databaseShutdown()
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