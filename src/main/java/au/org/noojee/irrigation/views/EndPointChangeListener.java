package au.org.noojee.irrigation.views;

import au.org.noojee.irrigation.entities.EndPoint;

public interface EndPointChangeListener
{

	void notifyHardOn(EndPoint endPoint);

	void notifyHardOff(EndPoint endPoint);

	void timerStarted(EndPoint endPoint);

	void timerFinished(EndPoint endPoint);


}
