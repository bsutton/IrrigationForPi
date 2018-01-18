package au.org.noojee.irrigation.types;

import org.junit.jupiter.api.Test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import au.org.noojee.irrigation.WebAppListener;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.EntityManagerUtil;
import au.org.noojee.irrigation.entities.GardenBed;

class ValveControllerTest
{

	@Test
	void test() throws InterruptedException
	{
		final GpioController gpio = GpioFactory.getInstance();

		for (Pin pin : RaspiPin.allPins())
		{
			PinState offState = PinState.HIGH;

			GpioPinDigitalOutput digitPin = gpio.provisionDigitalOutputPin(pin, offState);
			digitPin.setShutdownOptions(true, PinState.LOW);
		}

		Pin[] pins = RaspiPin.allPins();
		EndPoint masterValve = new EndPoint();
		masterValve.setEndPointName("Master Valve");
		masterValve.setPiPin(pins[0]);

		EndPoint valve1 = new EndPoint();
		valve1.setEndPointName("Bed1");
		valve1.setPiPin(pins[1]);
		
		EndPoint valve2 = new EndPoint();
		valve2.setEndPointName("Bed2");
		valve2.setPiPin(pins[2]);


		GardenBed bed1 = new GardenBed();
		bed1.setMasterValve(masterValve);
		bed1.setValve(valve1);

		GardenBed bed2 = new GardenBed();
		bed2.setMasterValve(masterValve);
		bed2.setValve(valve2);

		ValveController.turnOn(bed1);
		ValveController.turnOn(bed2);
		Thread.sleep(3000);
		ValveController.turnOff(bed1);
		Thread.sleep(10000);
		ValveController.turnOff(bed2);
		
		// wait for things to finish.
		Thread.sleep(40000);
	}

}
