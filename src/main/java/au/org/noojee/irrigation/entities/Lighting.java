package au.org.noojee.irrigation.entities;

import java.time.Duration;

import javax.persistence.Entity;
import javax.persistence.Table;

import au.org.noojee.irrigation.dao.LightingDao;

@Entity
@Table(name="tblLighting")
public class Lighting  extends GardenFeature
{
	EndPoint lightSwitch;

	// required by JPA
	public Lighting()
	{
		
	}
	
	public Lighting(EndPoint lightSwitch)
	{
		this.lightSwitch = lightSwitch;
	}
	
	@Override
	public String getName()
	{
		return lightSwitch.getEndPointName();
	}

	public boolean isOn()
	{
		return lightSwitch.isOn();
	}
	
	public Void softOff()
	{
		super.softOff();
		
		this.lightSwitch.hardOff();
		
		LightingDao daoLighting = new LightingDao();
		daoLighting.merge(this);

		
		return null;
	}


	public void softOn()
	{
		super.softOn();
		
		this.lightSwitch.hardOn();

	}



	public EndPoint getLightSwitch()
	{
		return lightSwitch;
	}

	





}
