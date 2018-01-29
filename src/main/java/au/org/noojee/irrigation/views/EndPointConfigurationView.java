package au.org.noojee.irrigation.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
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
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.types.EndPointBus;
import au.org.noojee.irrigation.types.GardenBedController;
import au.org.noojee.irrigation.types.PinStatus;
import au.org.noojee.irrigation.views.editors.EndPointEditorView;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureau;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureaus;
import au.org.noojee.irrigation.weather.bureaus.WeatherStation;

public class EndPointConfigurationView extends VerticalLayout
		implements SmartView, EndPointChangeListener, ViewChangeListener
{
	private static final long serialVersionUID = 1L;
	public static final String NAME = "EndPoints";
	public static final String LABEL = "End Points";

	private ComboBox<WeatherStation> weatherStation;
	private ComboBox<WeatherBureau> weatherBureau;
	private GridLayout endPointGrid;

	private Map<EndPoint, Switch> switchMap = new HashMap<>();

	private UI ui;

	public EndPointConfigurationView()
	{
		this.ui = UI.getCurrent();
	}

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
		Label headingLabel = new Label("End Points");
		headingLabel.setStyleName("i4p-heading");
		Responsive.makeResponsive(headingLabel);
		heading.addComponent(headingLabel);
		heading.setComponentAlignment(headingLabel, Alignment.TOP_CENTER);
		this.addComponent(heading);

		weatherBureau = new ComboBox<>("Weather Bureau");
		weatherBureau.setStyleName("i4p-combobox");
		Responsive.makeResponsive(weatherBureau);
		this.addComponent(weatherBureau);
		weatherBureau.setDataProvider(new ListDataProvider<>(WeatherBureaus.getBureaus()));
		weatherBureau.setItemCaptionGenerator(WeatherBureau::getCountryName);
		weatherBureau.addValueChangeListener(l -> buerauSelected(l));

		weatherStation = new ComboBox<>("Weather Station");
		weatherStation.setStyleName("i4p-combobox");
		Responsive.makeResponsive(weatherStation);

		this.addComponent(weatherBureau);

		Panel scrollPanel = new Panel();
		this.addComponent(scrollPanel);
		scrollPanel.setSizeFull();

		scrollPanel.setContent(buildEndPointGrid());
		this.setExpandRatio(scrollPanel, 1);

		Button addButton = new Button("Add", VaadinIcons.PLUS_CIRCLE);
		addButton.setStyleName("i4p-button");
		Responsive.makeResponsive(addButton);

		addButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		addButton.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(addButton);
		this.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
		addButton.addClickListener(l -> addEndPoint());

	}

	private Component buildEndPointGrid()
	{
		// Get a list of Already configured endPoints
		EndPointDao daoEndPoint = new EndPointDao();
		List<EndPoint> endPoints = daoEndPoint.getAll();

		endPointGrid = new GridLayout();
		endPointGrid.setMargin(new MarginInfo(false, false, false, true));
		endPointGrid.setStyleName("i4p-grid");
		endPointGrid.setSpacing(false);

		endPointGrid.setWidth("100%");
		endPointGrid.setHeightUndefined();
		endPointGrid.setColumns(3);

		for (EndPoint endPoint : endPoints)
		{
			EndPointBus.getInstance().addListener(endPoint, this);

			Label endPointNameLabel = new Label(endPoint.getEndPointName());
			endPointNameLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(endPointNameLabel);
			endPointGrid.addComponent(endPointNameLabel);

			endPointGrid.addComponent(createOnOffSwitch(endPoint));

			Button editButton = new Button("Edit", VaadinIcons.EDIT);
			editButton.setStyleName("i4p-button");
			Responsive.makeResponsive(editButton);

			editButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			editButton.setData(endPoint);
			editButton.addClickListener(e -> editEndPoint(e));
			endPointGrid.addComponent(editButton);

		}

		return endPointGrid;
	}

	private Switch createOnOffSwitch(EndPoint endPoint)
	{
		Switch endPointToggle = new Switch();
		endPointToggle.setStyleName("i4p-switch");
		Responsive.makeResponsive(endPointToggle);

		endPointToggle.setValue(endPoint.getCurrentStatus() == PinStatus.ON);

		endPointToggle.addValueChangeListener(e ->
			{
				if (e.getValue() == true)
					endPoint.hardOn();
				else
					endPoint.hardOff();
			});

		switchMap.put(endPoint, endPointToggle);

		return endPointToggle;
	}

	private void editEndPoint(ClickEvent e)
	{
		if (GardenBedController.isAnyValveRunning())
			Notification.show("Can't add EndPoint", "You can't edit an EndPoint whilst any valves are on.",
					Type.ERROR_MESSAGE);
		else
		{
			EndPoint endPoint = (EndPoint) e.getButton().getData();

			EndPointEditorView defineEndPointView = (EndPointEditorView) ((ControllerUI) UI.getCurrent())
					.getView(EndPointEditorView.NAME);
			defineEndPointView.setBean(endPoint);
			UI.getCurrent().getNavigator().navigateTo(EndPointEditorView.NAME);
		}
	}

	private void addEndPoint()
	{
		if (GardenBedController.isAnyValveRunning())
			Notification.show("Can't add EndPoint", "You can't add an EndPoint whilst any valves are on.",
					Type.ERROR_MESSAGE);
		else
		{
			EndPointEditorView defineEndPointView = (EndPointEditorView) ((ControllerUI) UI.getCurrent())
					.getView(EndPointEditorView.NAME);

			defineEndPointView.setBean(null);

			UI.getCurrent().getNavigator().navigateTo(EndPointEditorView.NAME);
		}

	}

	private void buerauSelected(ValueChangeEvent<WeatherBureau> l)
	{
		WeatherBureau bureau = l.getValue();

		this.weatherStation.setDataProvider(new ListDataProvider<>(bureau.getStations()));
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void notifyHardOn(EndPoint gardenBed)
	{
		// Handle notifications that the EndPoint has changed state
		Switch toggle = switchMap.get(gardenBed);
		toggle.setValue(true);

	}

	@Override
	public void notifyHardOff(EndPoint gardenBed)
	{

		// Handle notifications that the EndPoint has changed state
		Switch toggle = switchMap.get(gardenBed);
		if (ui.isAttached())
			ui.access(() ->
				{
					toggle.setValue(false);
				});
	}

	@Override
	public void afterViewChange(ViewChangeEvent event)
	{
		if (event.getOldView() == this)
			EndPointBus.getInstance().removeListener(this);
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event)
	{
		// We always let the view change.
		return true;
	}

}
