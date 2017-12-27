package au.org.noojee.irrigation;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
	public WebAppListener()
	{
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		System.out.println("My Vaadin web app is starting. ");

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
		System.out.println("Irriguation Manager is shutting down.");
		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and
		// scheduled tasks)
		final GpioController gpio = GpioFactory.getInstance();

		gpio.shutdown();

	}

}