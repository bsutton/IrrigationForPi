package au.org.noojee.irrigation.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import au.org.noojee.irrigation.types.Amperage;
import au.org.noojee.irrigation.types.EndPointType;
import au.org.noojee.irrigation.types.PinActivationType;

@Entity
public class Pin
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Name of the device attached to the pin.
	private String deviceName;
	private PinActivationType pinActiviationType;
	private EndPointType endPointType;
	private com.pi4j.io.gpio.Pin piPin;
	
	// The amount of current activating this pin causes the device to draw.
	private Amperage amps;
	
	
	public void setOn()
	{
		if (pinActiviationType == PinActivationType.HIGH_IS_ON)
			setPinHigh();
		else 
			setPinLow();
	}
	
	public void setOff()
	{
		if (pinActiviationType == PinActivationType.HIGH_IS_ON)
			setPinLow();
		else 
			setPinHigh();
	}
	
	
	private void setPinHigh()
	{
		final GpioController gpio = GpioFactory.getInstance();

		GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(piPin);

		gpioPin.setState(PinState.HIGH);
		gpioPin.high();
		
	}

	
	private void setPinLow()
	{
		final GpioController gpio = GpioFactory.getInstance();

		GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(piPin);

		gpioPin.setState(PinState.LOW);
		gpioPin.low();
		
	}

	

}
