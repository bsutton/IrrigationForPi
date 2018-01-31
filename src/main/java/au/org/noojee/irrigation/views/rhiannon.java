package au.org.noojee.irrigation.views;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.HistoryDao;
import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.entities.History;
import au.org.noojee.irrigation.util.Formatters;

public class rhiannon   extends VerticalLayout implements SmartView {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "History";
	
	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		this.removeAllComponents();
		
		HorizontalLayout headingLayout =new HorizontalLayout();
		this.addComponent(headingLayout);
		
		Label startDateHeading =new Label("Date/Time");
		headingLayout.addComponent(startDateHeading);
		startDateHeading.setWidth("40mm");
		startDateHeading.addStyleName(ValoTheme.LABEL_H3);

		
		Label durationHeading =new Label("Duration");
		headingLayout.addComponent(durationHeading);
		durationHeading.setWidth("20mm");
		durationHeading.addStyleName(ValoTheme.LABEL_H3);
		
		Label gardenHeading =new Label("Garden Bed");
		headingLayout.addComponent(gardenHeading);
		gardenHeading.addStyleName(ValoTheme.LABEL_H3);
		
		List<History> histories;
		
		HistoryDao daoHistory =new HistoryDao();
		
		histories =daoHistory.getAll();
		
		for (History history : histories)
		{
			HorizontalLayout historyHorizontal =new HorizontalLayout();
			this.addComponent(historyHorizontal);
			//Label 
			LocalDateTime startDate = history.getStart();
			Label startDateLabel =new Label(Formatters.format(startDate));
			historyHorizontal.addComponent(startDateLabel);
			startDateLabel.setWidth("40mm");
			
			Duration duration = history.getDuration();
			Label durationLabel =new Label(Formatters.format(duration));
			historyHorizontal.addComponent(durationLabel);
			durationLabel.setWidth("20mm");
			
			GardenFeature gardenFeature = history.getGardenFeature();
			Label gardenLabel =new Label(gardenFeature.getName());
			historyHorizontal.addComponent(gardenLabel);
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}



}
