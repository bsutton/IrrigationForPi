package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.dao.HistoryDao;
import au.org.noojee.irrigation.types.PinStatus;
import au.org.noojee.irrigation.types.ValveController;

@Entity
public class GardenBed
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	String name;
	String desription;
	History lastWatering;
	LocalDateTime nextWatering;

	int mostiureContent;

	// The raspberry pin that controls the valve
	// for this garden bed.
	EndPoint valve;

	// If this bed has a master valve that needs to be
	// activated to turn this garden bed on.
	EndPoint masterValve;

	// If we have a master valve we offer an option to bleed the pressure from the line
	// by turning the master valve off before we turn the garden bed valve off.
	private boolean bleedLine = false;

	transient private History currentHistory;

	public long getId()
	{
		return id;
	}

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
		return lastWatering;
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

	public boolean isBleedLine()
	{
		return bleedLine;
	}

	public void setBleedLine(boolean bleadLine)
	{
		this.bleedLine = bleadLine;
	}

	public void turnOff()
	{
		ValveController.turnOff(this);
	
		if (this.currentHistory != null)
		{
			this.currentHistory.endWateringEvent();
			this.lastWatering = this.currentHistory;

			HistoryDao daoHistory = new HistoryDao();
			daoHistory.persist(this.currentHistory);
			this.currentHistory = null;
			
			GardenBedDao daoGardenBed = new GardenBedDao();
			daoGardenBed.merge(this);
			
		}
	}

	public void turnOn()
	{
		this.currentHistory = new History(this);
		
		// save the last watering event.
		GardenBedDao daoGardenBed = new GardenBedDao();
		daoGardenBed.merge(this);
		
		ValveController.turnOn(this);

	}

	public EndPoint getPin()
	{
		return this.valve;
	}

	public Boolean isOn()
	{
		return this.valve.getCurrentStatus() == PinStatus.ON;
	}

}
