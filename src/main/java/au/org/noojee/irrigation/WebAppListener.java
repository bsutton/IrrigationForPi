package au.org.noojee.irrigation;

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
		

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();
		
		// Set default states for pins.
		for (Pin pin : RaspiPin.allPins())
		{
			GpioPinDigitalOutput digitPin = gpio.provisionDigitalOutputPin(pin, PinState.HIGH);
			digitPin.setShutdownOptions(true, PinState.LOW);
		}

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