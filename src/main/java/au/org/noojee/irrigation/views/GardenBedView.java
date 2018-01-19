package au.org.noojee.irrigation.views;

import java.util.List;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.entities.History;
import au.org.noojee.irrigation.util.Formatters;

public class GardenBedView extends VerticalLayout implements SmartView
{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "GardenBeds";
	public static final String LABEL = "Garden Beds";

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		this.removeAllComponents();

		List<GardenBed> gardenBeds;

		GardenBedDao daoGardenBed = new GardenBedDao();

		gardenBeds = daoGardenBed.getAll();

		Panel scrollPanel = new Panel();
		this.addComponent(scrollPanel);
		this.setExpandRatio(scrollPanel, 1);
		scrollPanel.setSizeFull();

		VerticalLayout gardenBedLayout = new VerticalLayout();
		scrollPanel.setContent(gardenBedLayout);

		gardenBedLayout.setSizeFull();

		for (GardenBed gardenBed : gardenBeds)
		{
			HorizontalLayout gardenBedHorizontal = new HorizontalLayout();
			gardenBedLayout.addComponent(gardenBedHorizontal);
			gardenBedHorizontal.setWidth("100%");

			Label nameLabel = new Label(gardenBed.getName());
			gardenBedHorizontal.addComponent(nameLabel);
			gardenBedHorizontal.setExpandRatio(nameLabel, 1.0f);

			History history = gardenBed.getLastWatering();
			if (history != null)
			{
				Label lastWateredLabel = new Label(Formatters.format(history.getStartDate().toLocalDate()));
				gardenBedHorizontal.addComponent(lastWateredLabel);

				Label durationLabel = new Label(Formatters.format(history.getDuration()));
				gardenBedHorizontal.addComponent(durationLabel);
			}

			gardenBedHorizontal.addComponent(createOnOffSwitch(gardenBed));

		}

	}

	private Switch createOnOffSwitch(GardenBed gardenBed)
	{
		Switch pinToggle = new Switch();
		pinToggle.setStyleName("i4p-switch");
		Responsive.makeResponsive(pinToggle);

		pinToggle.setValue(gardenBed.isOn());

		pinToggle.addValueChangeListener(e ->
			{
				if (e.getValue() == true)
				{
					gardenBed.turnOn();
				}
				else
					gardenBed.turnOff();
			});

		return pinToggle;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
