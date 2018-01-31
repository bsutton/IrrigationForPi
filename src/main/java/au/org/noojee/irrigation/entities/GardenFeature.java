package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.org.noojee.irrigation.controllers.EndPointBus;
import au.org.noojee.irrigation.controllers.TimerControl;
import au.org.noojee.irrigation.dao.GardenFeatureDao;
import au.org.noojee.irrigation.views.TimerNotification;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class GardenFeature
{
	@SuppressWarnings("unused")
	transient private static Logger logger = LogManager.getLogger();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private long id;

	@Version
	private int version;

	@OneToMany(mappedBy = "gardenFeature", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("eventStart DESC")
	private List<History> historyList;

	private transient History currentHistory;
	
	

	public abstract boolean isOn();

	public abstract String getName();
	
	abstract public EndPoint getPrimaryEndPoint();

	public long getId()
	{
		return id;
	}

	public void softOn()
	{
		this.currentHistory = new History(this);
	}

	public void runForTime(String description, Duration runTime, TimerNotification timerNotifaction)
	{
		TimerControl.startTimer(this, description, runTime, feature -> feature.timerCompleted(), timerNotifaction);

		this.softOn();
	}

	private Void timerCompleted()
	{
		EndPointBus.getInstance().timerFinished(getPrimaryEndPoint());

		softOff();
		
		return null;
	}

	public Void softOff()
	{

		if (this.currentHistory != null)
		{
			this.currentHistory.markEventComplete();
			this.addHistory(this.currentHistory);

			// So the garden bed we are dealing with will be detached.
			// So we need to do a merge but the merge returns a new object
			// which is inconvenient as this garden bed is wedged everywhere.
			// so as a hack (which hopefully won't burn us) we update the db
			// but also update our in memory version with the SAME history object.
			GardenFeatureDao daoFeature = new GardenFeatureDao();
			GardenFeature feature = daoFeature.getById(this.id);
			feature.addHistory(this.currentHistory);
			daoFeature.merge(feature);
			this.currentHistory = null;
		}
		
		
//		TimerControl.cancelTimer(this);


		return null;
	}

	void addHistory(History history)
	{
		historyList.add(0, history);
	}

	void removeHistory(History history)
	{
		historyList.remove(history);
		history.clearGardenFeature();
	}

	public History getLastEvent()
	{
		return (this.historyList.size() > 0 ? this.historyList.get(0) : null);
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
		GardenFeature other = (GardenFeature) obj;
		if (id != other.id)
			return false;
		return true;
	}

	

}
