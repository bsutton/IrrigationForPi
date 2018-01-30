package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Future;

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

import au.org.noojee.irrigation.util.Delay;
import au.org.noojee.irrigation.views.TimerNotification;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class GardenFeature
{
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

	transient private Future<Void> timerFuture = null;

	transient private TimerNotification timerNotifaction;

	public abstract boolean isOn();

	public abstract String getName();

	public long getId()
	{
		return id;
	}

	public void softOn()
	{
		this.currentHistory = new History(this);
	}

	public void runForTime(Duration runTime, TimerNotification timerNotifaction)
	{
		logger.error("Starting Timer for : " + this);

		// cancel any existing timer first.
		if (timerFuture != null)
		{
			timerFuture.cancel(true);
			timerNotifaction.timerFinished(this);
		}

		this.timerNotifaction = timerNotifaction;
		
		this.softOn();

		// Run the bed until the timer goes off.
		timerFuture = Delay.delay(runTime, this, bed -> this.softOff());

	}

	public Void softOff()
	{
		if (timerFuture != null)
		{
			timerFuture.cancel(false);
			timerNotifaction.timerFinished(this);
		}

		if (this.currentHistory != null)
		{
			this.currentHistory.markEventComplete();
			this.addHistory(this.currentHistory);
			this.currentHistory = null;
		}

		return null;
	}

	void addHistory(History history)
	{
		historyList.add(history);
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
