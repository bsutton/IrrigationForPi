package au.org.noojee.irrigation;

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
import au.org.noojee.irrigation.dao.MyEntityManagerUtil;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.types.GardenBedController;

/**
 * @author Brett Sutton
 */
@WebListener
public class WebAppListener implements ServletContextListener
{
	public static Logger logger;
	private boolean databaseInitialised;

	public WebAppListener()
	{

	}

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		System.out.println("Pi-gation is starting.");

		System.out.println("Pi-gation: starting logger");

		logger = LogManager.getLogger();

		logger.info("PI PLATFORM: " + System.getenv("PI4J_PLATFORM"));
		logger.info("Simulated PLATFORM - simulated: " + System.getenv("SimulatedPlatform"));

		MyEntityManagerUtil.init();

		databaseInitialised = true;

		provisionPins();

		GardenBedController.init();

	}

	public static void provisionPins()
	{
		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		EndPointDao daoEndPoint = new EndPointDao();
		List<au.org.noojee.irrigation.entities.EndPoint> pins = daoEndPoint.getAll();

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

		// Only shutdown the db if we actually got to the point of initialising it.
		if (databaseInitialised)
			MyEntityManagerUtil.databaseShutdown();

		LogManager.shutdown();
	}

}