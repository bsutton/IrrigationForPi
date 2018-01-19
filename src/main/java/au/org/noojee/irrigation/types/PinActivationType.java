package au.org.noojee.irrigation.types;

import com.pi4j.io.gpio.PinState;

public enum PinActivationType
{
	HIGH_IS_ON("High is On", PinState.HIGH, PinState.LOW), LOW_IS_ON("Low is On", PinState.LOW, PinState.HIGH);
	
	private String label;
	private PinState onState;

	private PinState offState;
	
	PinActivationType(String label, PinState onState, PinState offState)
	{
		this.label = label;
		this.onState = onState;
		this.offState = offState;
	}

	public PinState getOffState()
	{
		return this.offState;
	}
	
	public String getLabel()
	{
		return label;
	}

	public PinState getOnState()
	{
		return onState;
	}

}
