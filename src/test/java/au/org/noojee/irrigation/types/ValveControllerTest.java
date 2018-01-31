package au.org.noojee.irrigation.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import au.org.noojee.irrigation.controllers.GardenBedController;
import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.dao.MyEntityManagerUtil;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;

public class ValveControllerTest
{
	static Logger logger = LogManager.getLogger();

	@Test
	public void test()
	{
		try
		{
			MyEntityManagerUtil.initTest();

			EndPointDao daoEndPoint = new EndPointDao();
			GardenBedDao daoGardenBed = new GardenBedDao();

			daoGardenBed.deleteAll();
			daoEndPoint.deleteAll();

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
			masterValve.setEndPointType(EndPointType.MasterValve);
			masterValve.setPinActiviationType(PinActivationType.LOW_IS_ON);
			daoEndPoint.persist(masterValve);

			EndPoint valve1 = new EndPoint();
			valve1.setEndPointName("Bed1");
			valve1.setPiPin(pins[1]);
			valve1.setEndPointType(EndPointType.Valve);
			valve1.setPinActiviationType(PinActivationType.LOW_IS_ON);
			daoEndPoint.persist(valve1);

			EndPoint valve2 = new EndPoint();
			valve2.setEndPointName("Bed2");
			valve2.setPiPin(pins[2]);
			valve2.setEndPointType(EndPointType.Valve);
			valve2.setPinActiviationType(PinActivationType.LOW_IS_ON);
			daoEndPoint.persist(valve2);

			GardenBed bed1 = new GardenBed();
			bed1.setName("bed1");
			bed1.setMasterValve(masterValve);
			bed1.setValve(valve1);
			daoGardenBed.persist(bed1);

			GardenBed bed2 = new GardenBed();
			bed2.setName("bed2");
			bed2.setMasterValve(masterValve);
			bed2.setValve(valve2);
			daoGardenBed.persist(bed2);

		
			logger.error("Running NO drain line test");
			runTestSequence(bed1, bed2, masterValve);
			
			assert bed1.getValve().isOff() : "bed 1 should be off";
			assert bed1.getValve().isOff() : "bed 2 should be off";
			assert masterValve.isOff() : "master valve should be off";
		
	

			// Now turn drainLine on

			masterValve.setDrainLine(true);
			daoEndPoint.merge(masterValve);
			// We changed the master valves setting so we need to re-init the controller.
			GardenBedController.init();

			logger.error("Running drain line test");
			runTestSequence(bed1, bed2, masterValve);

			// wait for things to finish.
			Thread.sleep(40000);

			MyEntityManagerUtil.databaseShutdown();
		}
		catch (Throwable e)
		{
			logger.error(e, e);

		}

	}

	private void runTestSequence(GardenBed bed1, GardenBed bed2, EndPoint masterValve) throws InterruptedException
	{
		// Now we have created the beds and the valves we can init the controller.
		GardenBedController.init();

		logger.error("step 1");
		bed1.softOn();
		assert (masterValve.isOn());
		logger.error("step 2");
		bed2.softOn();
		assert (masterValve.isOn());
		logger.error("step 3");
		bed1.softOff();
		assert masterValve.isOn() : "bed1 is on so master valve should be on.";
		assert (bed1.getValve().isOff());
		logger.error("step 4");
		bed2.softOff();
		assert (masterValve.isOff());
		if (masterValve.isDrainingLine())
			assert (bed2.isOn());
		else
			assert (bed2.isOff());
		logger.error("step 5");
		bed1.softOn();
		assert (masterValve.isOn());
		logger.error("step 6");
		bed1.softOff();
		assert (masterValve.isOff());
		logger.error("step 7");
		bed1.softOn();
		logger.error("step 8");
		bed1.softOff();
		assert (masterValve.isOff());
		logger.error("step 9");
		bed1.softOn();
		logger.error("step 10");
		bed1.softOff();
		logger.error("step 11");
		bed2.softOn();
		logger.error("step 12");
		bed1.softOff();
		assert masterValve.isOn() : "bed2 is still on, so master valve should be on"; 
		logger.error("step 13");
		bed1.softOn();
		logger.error("step 14");
		bed2.softOn();
		logger.error("step 15");
		bed2.softOff();
		assert (masterValve.isOn());
		logger.error("step 16");
		bed1.softOff();
		assert (masterValve.isOff());
		logger.error("step 17");
		bed1.softOn();
		assert (masterValve.isOn());
		logger.error("step 18");
		bed2.softOn();
		assert (masterValve.isOn());
		logger.error("step 19");
		bed1.softOff();
		assert (masterValve.isOn());
		logger.error("step 20");
		bed2.softOff();
		assert (masterValve.isOff());

	}
	//
	// public static void main(String[] args)
	// {
	// try
	// {
	// new ValveControllerTest().test();
	// }
	// catch (InterruptedException e)
	// {
	// logger.error(e,e);
	// }
	// }
	//
}
