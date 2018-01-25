package au.org.noojee.irrigation.views;

import au.org.noojee.irrigation.entities.GardenBed;

public interface ValveChangeListener
{

	void notifyOn(GardenBed gardenBed);

	void notifyOff(GardenBed gardenBed);

}
