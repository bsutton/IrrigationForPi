package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tblHistory")
public class History
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	LocalDateTime wateringEvent;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gardenBed_id")
	GardenBed gardenBed;
	Duration duration;
	
	// required by JPA
	public History()
	{
		
	}
	public History(GardenBed gardenBed)
	{
		this.gardenBed = gardenBed;
		this.wateringEvent = LocalDateTime.now();
	}
	public long getId()
	{
		return id;
	}
	public void endWateringEvent()
	{
		LocalDateTime end = LocalDateTime.now();
		
		
		this.duration = Duration.between(wateringEvent, end);
		
	}
	public LocalDateTime getStartDate()
	{
		return wateringEvent;
	}
	public Duration getDuration()
	{
		return this.duration;
	}
	public GardenBed getGardenBed()
	{
		return this.gardenBed;
	}
	public void clearGardenBed()
	{
		this.gardenBed = null;
		
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
