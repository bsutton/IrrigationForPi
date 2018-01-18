package au.org.noojee.irrigation.views;

import java.util.List;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.ControllerUI;
import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.views.editors.GardenBedEditorView;

public class GardenBedConfigurationView extends VerticalLayout implements SmartView
{
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Garden Bed Configuration";
	public static final String LABEL = "Garden Beds";

	private GridLayout gardenBedGrid;

	public void enter(ViewChangeEvent event)
	{
		this.removeAllComponents();
		build();
	}

	void build()
	{
		this.setSizeFull();
		this.setMargin(false);

		HorizontalLayout heading = new HorizontalLayout();
		heading.setWidth("100%");
		Label headingLabel = new Label("Garden Beds");
		headingLabel.setStyleName("i4p-heading");
		Responsive.makeResponsive(headingLabel);
		heading.addComponent(headingLabel);
		heading.setComponentAlignment(headingLabel, Alignment.TOP_CENTER);
		this.addComponent(heading);

		Panel scrollPanel = new Panel();
		this.addComponent(scrollPanel);
		scrollPanel.setSizeFull();

		scrollPanel.setContent(buildGrid());
		this.setExpandRatio(scrollPanel, 1);

		Button addButton = new Button("Add", VaadinIcons.PLUS_CIRCLE);
		addButton.setStyleName("i4p-button");
		Responsive.makeResponsive(addButton);

		addButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		addButton.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(addButton);
		this.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
		addButton.addClickListener(l -> addGardenBed());

	}

	private Component buildGrid()
	{
		// Get a list of Already configured Garden Beds
		GardenBedDao daoGardenBed = new GardenBedDao();
		List<GardenBed> gardenBeds = daoGardenBed.getAll();

		gardenBedGrid = new GridLayout();
		gardenBedGrid.setMargin(new MarginInfo(false, false, false, true));
		gardenBedGrid.setStyleName("i4p-grid");
		gardenBedGrid.setSpacing(false);

		gardenBedGrid.setWidth("100%");
		gardenBedGrid.setHeightUndefined();
		gardenBedGrid.setColumns(3);


		for (GardenBed gardenBed : gardenBeds)
		{

			Label gardenBedNameLabel = new Label(gardenBed.getName());
			gardenBedNameLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(gardenBedNameLabel);
			gardenBedGrid.addComponent(gardenBedNameLabel);

			gardenBedGrid.addComponent(createOnOffSwitch(gardenBed));

			Button editButton = new Button("Edit", VaadinIcons.EDIT);
			editButton.setStyleName("i4p-button");
			Responsive.makeResponsive(editButton);

			editButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			editButton.setData(gardenBed);
			editButton.addClickListener(e -> editGardenBed(e));
			gardenBedGrid.addComponent(editButton);
		}

		return gardenBedGrid;
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

	private void editGardenBed(ClickEvent e)
	{
		GardenBed gardenBed = (GardenBed) e.getButton().getData();

		GardenBedEditorView editGardenBedView = (GardenBedEditorView) ((ControllerUI) UI.getCurrent())
				.getView(GardenBedEditorView.NAME);
		editGardenBedView.setBean(gardenBed);

		UI.getCurrent().getNavigator().navigateTo(GardenBedEditorView.NAME);
	}

	private void addGardenBed()
	{
		// Check that we have at least one valve configured

		EndPointDao daoEndPoint = new EndPointDao();
		if (daoEndPoint.getAllValves().size() == 0)
		{
			Notification.show("Unable to Add Garden Beds"
					, "You must configure at least one 'Valve' via the 'End Point' configuration before you can add a garden bed."
					, Type.ERROR_MESSAGE);
		}
		else
		{
			GardenBedEditorView editGardenBedView = (GardenBedEditorView) ((ControllerUI) UI.getCurrent())
					.getView(GardenBedEditorView.NAME);

			editGardenBedView.setBean(null);

			UI.getCurrent().getNavigator().navigateTo(GardenBedEditorView.NAME);
		}

	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
