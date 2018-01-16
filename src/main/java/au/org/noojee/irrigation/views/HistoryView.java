package au.org.noojee.irrigation.views;

import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.dao.HistoryDao;
import au.org.noojee.irrigation.entities.History;

public class HistoryView   extends VerticalLayout implements SmartView {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "History";
	
	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		
		List<History> histories;
		
		HistoryDao daoHistory =new HistoryDao();
		
		histories =daoHistory.getAll();
		VerticalLayout historyLayout =new VerticalLayout();
		
		for (History history : histories)
		{
			HorizontalLayout historyHorizontal =new HorizontalLayout();
			historyLayout.addComponent(historyHorizontal);
			//Label 
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}



}
