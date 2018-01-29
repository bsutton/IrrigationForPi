package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Records the start time and duration each time an end point is activated.
 * @author bsutton
 *
 */
@Entity
@Table(name="tblHistory")
public class History
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private long id;
	
	 @Version
     private int version;


	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gardenFeature_id")
	GardenFeature gardenFeature;

	
	LocalDateTime eventStart;
	Duration eventDuration;
	
	// required by JPA
	public History()
	{
		
	}
	public History(GardenFeature device)
	{
		this.gardenFeature = device;
		this.eventStart = LocalDateTime.now();
	}
	public long getId()
	{
		return id;
	}
	public void markEventComplete()
	{
		LocalDateTime end = LocalDateTime.now();
		
		
		this.eventDuration = Duration.between(eventStart, end);
		
	}
	public LocalDateTime getStart()
	{
		return eventStart;
	}
	public Duration getDuration()
	{
		return this.eventDuration;
	}
	public GardenFeature getGardenFeature()
	{
		return this.gardenFeature;
	}
	public void clearGardenFeature()
	{
		this.gardenFeature = null;
		
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
		History other = (History) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
