package au.org.noojee.irrigation.widgets.client.timerLabel;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;

public class TimerLabelState extends AbstractComponentState
{
	private static final long serialVersionUID = -9139716129071399826L;



	@DelegateToWidget
	public Long epoc;

	@DelegateToWidget
	public Boolean started;

	@DelegateToWidget
	public TimeThreshold[] thresholds;


	@DelegateToWidget
	public String style;


}
