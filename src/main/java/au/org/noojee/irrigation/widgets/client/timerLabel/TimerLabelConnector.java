package au.org.noojee.irrigation.widgets.client.timerLabel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@Connect(au.org.noojee.irrigation.widgets.timerLabel.TimerLabelComponent.class)
// NO_UCD
public class TimerLabelConnector extends AbstractComponentConnector
{

	private static final long serialVersionUID = 3279348308010471050L;
	TimerLabelServerRpc rpc = RpcProxy.create(TimerLabelServerRpc.class, this);

	public TimerLabelConnector()
	{
		registerRpc(TimerLabelClientRpc.class, new TimerLabelClientRpc()
		{

			private static final long serialVersionUID = -4175656221548699772L;

			@Override
			public void start()
			{
				getWidget().start();

			}

			@Override
			public void stop()
			{
				getWidget().stop();

			}

			@Override
			public void setInitialValue(TimeThreshold[] thresholds)
			{
				getWidget().setInitialValue(thresholds);

			}

		});

	}

	@Override
	protected Widget createWidget()
	{
		TimerLabelWidget widget = GWT.create(TimerLabelWidget.class);
		widget.setRpc(rpc);

		return widget;
	}

	@Override
	public TimerLabelWidget getWidget()
	{
		return (TimerLabelWidget) super.getWidget();
	}

	@Override
	public TimerLabelState getState()
	{
		return (TimerLabelState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent)
	{
		super.onStateChanged(stateChangeEvent);

		getWidget().restoreState(getState());

	}

}
