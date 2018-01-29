package au.org.noojee.irrigation.widgets.client.timerLabel;

import com.vaadin.shared.communication.ClientRpc;

public interface TimerLabelClientRpc extends ClientRpc
{
	// messages from server to client

	public void start();

	public void stop();

	public void setInitialValue( TimeThreshold[] thresholds);
}
