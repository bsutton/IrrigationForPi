package au.org.noojee.irrigation.types;

public enum PinActivationType
{
	HIGH_IS_ON("High is On"), LOW_IS_ON("Low is On");
	
	String label;
	
	PinActivationType(String label)
	{
		this.label = label;
	}
	
}
