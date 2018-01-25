package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.types.PinStatus;
import au.org.noojee.irrigation.types.ValveController;
import au.org.noojee.irrigation.util.Delay;
import au.org.noojee.irrigation.views.ValveChangeListener;

@Entity
@Table(name="tblGardenBed")
public class GardenBed
{
	private static Logger logger = LogManager.getLogger();

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique=true)
	String name;
	String desription;
	LocalDateTime nextWatering;

	int mostiureContent;

	// The raspberry pin that controls the valve
	// for this garden bed.
	EndPoint valve;

	// If this bed has a master valve that needs to be
	// activated to turn this garden bed on.
	EndPoint masterValve;
	
	@OneToMany(mappedBy = "gardenBed", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("wateringEvent DESC")
	List<History> historyList;


	private transient History currentHistory;


	private transient Future<Void> timerFuture = null;


	private transient ValveChangeListener valveChangeListener;

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
		return (this.historyList.size() > 0 ? this.historyList.get(0) : null);
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


	public void runForTime(Duration runTime, ValveChangeListener valveChangeListener)
	{
		logger.error("Starting Timer for : " + this);

		// cancel any existing timer first.
		if (timerFuture != null)
			timerFuture.cancel(true);
		
		this.valveChangeListener = valveChangeListener;
		
		this.turnOn();
		
		// Run the bed until the timer goes off.
		timerFuture = Delay.delay(runTime, this, bed -> bed.turnOff());

		
	}



	public Void turnOff()
	{
		if (timerFuture != null)
			timerFuture.cancel(false);
		
		if (this.valveChangeListener != null)
			this.valveChangeListener.notifyOff(this);
		
		ValveController.turnOff(this);
	
		if (this.currentHistory != null)
		{
			this.currentHistory.endWateringEvent();
			this.addHistory(this.currentHistory);

//			HistoryDao daoHistory = new HistoryDao();
//			daoHistory.persist(this.currentHistory);
			this.currentHistory = null;
			
			GardenBedDao daoGardenBed = new GardenBedDao();
			daoGardenBed.merge(this);
			
		}
		
		return null;
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
		return (this.valve == null ? false : this.valve.getCurrentStatus() == PinStatus.ON);
	}
	
	public boolean isOff()
	{
		
		return !isOn();
	}
	
	void addHistory(History history)
	{
		historyList.add(history);
	}
	
	void removeHistory(History history)
	{
		historyList.remove(history);
		history.clearGardenBed();
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
		GardenBed other = (GardenBed) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "GardenBed [id=" + id + ", name=" + name + ", desription=" + desription + ", lastWatering="
				 + nextWatering + ", mostiureContent=" + mostiureContent + ", valve="
				+ valve + ", masterValve=" + masterValve + "]";
	}


}
