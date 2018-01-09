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

import au.org.noojee.irrigation.dao.PinDao;
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

		

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();
		
		PinDao daoPin = new PinDao();
		List<au.org.noojee.irrigation.entities.Pin> pins = daoPin.getAll();
		
		// Set default states for pins.
		for (Pin pin : RaspiPin.allPins())
		{
			PinState offState = PinState.LOW;
			au.org.noojee.irrigation.entities.Pin configuredPin = getConfiguredPin(pin, pins);
			if (configuredPin != null)
				offState = configuredPin.getPinActiviationType().getOffState();
				
			GpioPinDigitalOutput digitPin = gpio.provisionDigitalOutputPin(pin, offState);
			digitPin.setShutdownOptions(true, PinState.LOW);
		}

	}
	
	au.org.noojee.irrigation.entities.Pin getConfiguredPin(Pin pin, List<au.org.noojee.irrigation.entities.Pin> pins)
	{
		au.org.noojee.irrigation.entities.Pin configuredPin = null;
		
		for (au.org.noojee.irrigation.entities.Pin checkPin : pins)
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

	}

}