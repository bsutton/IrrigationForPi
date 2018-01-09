package au.org.noojee.irrigation.views;

import com.vaadin.ui.VerticalLayout;

public class ScheduleView   extends VerticalLayout implements SmartView {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "Schedule";
	
	@Override
	public String getName()
	{
		return NAME;
	}



}
