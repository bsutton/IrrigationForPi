package au.org.noojee.irrigation.views;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.dao.HistoryDao;
import au.org.noojee.irrigation.entities.History;
import au.org.noojee.irrigation.util.Formatters;

public class OverviewView   extends VerticalLayout implements SmartView {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "Overview";
	
	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		
		GardenBedDao daoGardenBed =new GardenBedDao();
		
		if (daoGardenBed.getAll().size() == 0)
		{
			buildGettingStartedView();
		}
		else
			buildOverview();
	}
	
	private void buildGettingStartedView()
	{
		EndPointDao daoEndPoint =new EndPointDao();
		
		boolean endPointsExists = daoEndPoint.getAll().size() != 0;
		String message = "";
		
		this.removeAllComponents();
		this.setSizeFull();
		

		if (!endPointsExists)
		{
			message = "To get started you need to define one or more End Points.<br>"
					+"<br>"
					+ "An End Point is a Valve or Light and defines which Raspberry PI Pin the light or valve is attached to.<br>"
					+"<br>"
					+ "To configure an End Point select the 'Configuration' menu and then select 'End Points'<br>"
					+"<br>"
					+ "Once you have an End Point configured you need to define each Light and Garden Bed.<br>"
					+"<br>"
					+ "To Configure a Garden Bed select the 'Configuration' menu and then select 'Garden Beds'<br>";
		}
		else
		{
			message = "To get started you need to define one or more Garden Beds."
					+""
					+ "To Configure a Garden Bed select the 'Configuration' menu and then select 'Garden Beds'";
		}
		
		Label messageLabel = new Label("<p>" + message + "</p>");
		messageLabel.setSizeFull();
		messageLabel.setContentMode(ContentMode.HTML);
		this.addComponent(messageLabel);

					
		
	}

	void buildOverview()
	{
		this.removeAllComponents();
		this.setSizeUndefined();
		
		
		Label currentTemp = new Label("Current Temp: 21 c");
		this.addComponent(currentTemp);

		Label forecast = new Label("Forecast");
		this.addComponent(forecast);
		Label forecastHigh = new Label("High: 25 c");
		this.addComponent(forecastHigh);
		Label forecastLow = new Label("Low: 12 c");
		this.addComponent(forecastLow);
		
		Label rain = new Label("Rain ");
		this.addComponent(rain);
		Label rain24Hours = new Label("Last 24 Hours: 4mm");
		this.addComponent(rain24Hours);
		Label rain7Days= new Label("Last 7 days: 21mm");
		this.addComponent(rain7Days);
		
		
		
		Label wateringEvents = new Label("Watering Events ");
		this.addComponent(wateringEvents);

		List<History> histories;
		
		HistoryDao daoHistory =new HistoryDao();
		
		histories =daoHistory.getAll();
		histories = histories.stream().limit(5).collect(Collectors.toList());
		
		
		for (History history : histories)
		{
			HorizontalLayout historyHorizontal =new HorizontalLayout();
			this.addComponent(historyHorizontal);
			Label startDate = new Label(Formatters.format(history.getStartDate()));
			historyHorizontal.addComponent(startDate);
			Label duration = new Label(Formatters.format(history.getDuration()));
			historyHorizontal.addComponent(duration);
			Label gardenBed = new Label(history.getGardenBed().getName());
			historyHorizontal.addComponent(gardenBed);
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}



}
