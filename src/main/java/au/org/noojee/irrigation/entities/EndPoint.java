package au.org.noojee.irrigation.entities;

import java.time.Duration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinProvider;

import au.org.noojee.irrigation.types.Amperage;
import au.org.noojee.irrigation.types.EndPointBus;
import au.org.noojee.irrigation.types.EndPointType;
import au.org.noojee.irrigation.types.PinActivationType;
import au.org.noojee.irrigation.types.PinStatus;


@Entity
@Table(name="tblEndPoint")
public class EndPoint
{
	transient private static final Logger logger = LogManager.getLogger();
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
    private long id;
    
	 @Version
     private int version;


    // Name of the device attached to the pin.
    @Column(unique=true)
	private String endPointName;
	private EndPointType endPointType;
	private PinActivationType activationType;
	
	// We store the gpio pin no. here.
	@Column(unique=true)
	private int pinNo;
	
	// If we are a master valve we offer an option to bleed the pressure from the line
	// by turning the master valve off before we turn the garden bed valve off.
	private boolean bleedLine = false;
	
	// The amount of current activating this pin causes the device to draw.
	private Amperage startAmps;
	
	// the amount of current the device draws when this pin is active (post startup spike).
	private Amperage runningAmps;
	
	// The amount of time the 'startAmps' is drawn once the pin is activiated before
	// the current draw settles to the 'runningAmps'
	private Duration startupInterval;
	
	public Void hardOn()
	{
		if (activationType == PinActivationType.HIGH_IS_ON)
			setPinHigh();
		else 
			setPinLow();
		
		logger.error("Pin " + pinNo + " for EndPoint: " + (this.endPointType == EndPointType.MasterValve ? "(MasterValve)" : "" ) + this.endPointName + " set On.");
		
		EndPointBus.getInstance().notifyHardOn(this);

		return null;
	}
	
	public Void hardOff()
	{
		if (activationType == PinActivationType.HIGH_IS_ON)
			setPinLow();
		else 
			setPinHigh();

		logger.error("Pin " + pinNo + " for EndPoint: " + (this.endPointType == EndPointType.MasterValve ? "(MasterValve)" : "" ) + this.endPointName + " set Off.");
		
		EndPointBus.getInstance().notifyHardOff(this);

		return null;
	}
	
	public boolean isBleedLine()
	{
		return bleedLine;
	}

	public void setBleedLine(boolean bleadLine)
	{
		this.bleedLine = bleadLine;
	}
	
	private void setPinHigh()
	{
		GpioPinDigitalOutput gpioPin = this.getPiPin(this.pinNo);
		gpioPin.high();
		
	}
	
	private void setPinLow()
	{
		GpioPinDigitalOutput gpioPin = this.getPiPin(this.pinNo);
		gpioPin.low();
		
	}


	public void setPiPin(com.pi4j.io.gpio.Pin piPin)
	{
		
		this.pinNo = piPin.getAddress();
	}

	public com.pi4j.io.gpio.Pin getPiPin()
	{
		return 	PinProvider.getPinByAddress(pinNo);
	}

	
	public void setEndPointName(String endPointName)
	{
		this.endPointName = endPointName;
	}

	public void setPinActiviationType(PinActivationType pinActiviationType)
	{
		this.activationType = pinActiviationType;
	}

	public void setEndPointType(EndPointType endPointType)
	{
		this.endPointType = endPointType;
	}


	public void setStartAmps(Amperage startAmps)
	{
		this.startAmps = startAmps;
	}

	public void setRunningAmps(Amperage runningAmps)
	{
		this.runningAmps = runningAmps;
	}

	public void setStartupInterval(Duration startupInterval)
	{
		this.startupInterval = startupInterval;
	}
	
	public String getEndPointName()
	{
		return this.endPointName;
	}

	public long getId()
	{
		return id;
	}


	public EndPointType getEndPointType()
	{
		return this.endPointType;
	}

	public PinActivationType getPinActiviationType()
	{
		return this.activationType;
	}

	public PinStatus getCurrentStatus()
	{
		final GpioController gpio = GpioFactory.getInstance();
		com.pi4j.io.gpio.Pin internalPin = PinProvider.getPinByAddress(pinNo);

		GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(internalPin);

		return PinStatus.getStatus(this, gpioPin.isHigh());
	}

	public int getPinNo()
	{
		return this.pinNo;
	}

	public boolean isOn()
	{
		return getCurrentStatus() == PinStatus.ON;
	}


	private com.pi4j.io.gpio.GpioPinDigitalOutput getPiPin(int pinNo)
	{
		final GpioController gpio = GpioFactory.getInstance();
		com.pi4j.io.gpio.Pin internalPin = PinProvider.getPinByAddress(pinNo);

		GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(internalPin);
		
		return gpioPin;
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EndPoint other = (EndPoint) obj;
		if (id != other.id)
			return false;
		return true;
	}



	@Override
	public String toString()
	{
		return "EndPoint [id=" + id + ", endPointName=" + endPointName + ", endPointType=" + endPointType
				+ ", activationType=" + activationType + ", pinNo=" + pinNo + ", startAmps=" + startAmps
				+ ", runningAmps=" + runningAmps + ", startupInterval=" + startupInterval + "]";
	}

	public boolean isOff()
	{
		return !isOn();
	}

	


}
