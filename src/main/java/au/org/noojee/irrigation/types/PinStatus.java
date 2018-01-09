package au.org.noojee.irrigation.types;

import au.org.noojee.irrigation.entities.Pin;

public enum PinStatus
{
	ON, OFF;

	public static PinStatus getStatus(Pin pin, boolean isHigh)
	{
		PinStatus status;
		if (pin.getPinActiviationType() == PinActivationType.LOW_IS_ON)
		{
			if (isHigh)
				status = PinStatus.OFF;
			else
				status = PinStatus.ON;
		}
		else
		{
			if (isHigh)
				status = PinStatus.ON;
			else
				status = PinStatus.OFF;
		}

		return status;
	}
}
