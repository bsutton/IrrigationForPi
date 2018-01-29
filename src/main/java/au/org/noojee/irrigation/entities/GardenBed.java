package au.org.noojee.irrigation.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.types.GardenBedController;
import au.org.noojee.irrigation.types.PinStatus;

@Entity
@Table(name="tblGardenBed")
public class GardenBed extends GardenFeature
{
	@SuppressWarnings("unused")
	transient private static Logger logger = LogManager.getLogger();


	@Column(unique=true)
	String name;
	String desription;
	LocalDateTime nextWatering;

	int mostiureContent;

	// The End Point that defines the valve
	// for this garden bed.
	EndPoint valve;

	// If this bed has a master valve that needs to be
	// activated to turn this garden bed on.
	EndPoint masterValve;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDesription()
	{
		return desription;
	}

	public History getLastWatering()
	{
		return this.getLastEvent();
	}

	public LocalDateTime getNextWatering()
	{
		return nextWatering;
	}


	public int getMostiureContent()
	{
		return mostiureContent;
	}

	public EndPoint getValve()
	{
		return valve;
	}

	public void setValve(EndPoint valve)
	{
		this.valve = valve;
	}

	public EndPoint getMasterValve()
	{
		return masterValve;
	}

	public void setMasterValve(EndPoint masterValve)
	{
		this.masterValve = masterValve;
	}


	public Void softOff()
	{
		super.softOff();
		
		GardenBedController.softOff(this);
		
		GardenBedDao daoGardenBed = new GardenBedDao();
		daoGardenBed.merge(this);

		return null;
	}

	public void softOn()
	{
		super.softOn();

		// save the last watering event.
		GardenBedDao daoGardenBed = new GardenBedDao();
		daoGardenBed.merge(this);
		
		GardenBedController.softOn(this);
	}
	


	public EndPoint getPin()
	{
		return this.valve;
	}

	public boolean isOn()
	{
		return (this.valve == null ? false : this.valve.getCurrentStatus() == PinStatus.ON);
	}
	
	public boolean isOff()
	{
		
		return !isOn();
	}
	
	@Override
	public String toString()
	{
		return "GardenBed [id=" + getId() + ", name=" + name + ", desription=" + desription + ", lastWatering="
				 + nextWatering + ", mostiureContent=" + mostiureContent + ", valve="
				+ valve + ", masterValve=" + masterValve + "]";
	}


}
