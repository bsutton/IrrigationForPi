package au.org.noojee.irrigation.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class History
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	LocalDateTime wateringEvent;
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
}
