package au.org.noojee.irrigation.views;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.select.Collector;
import org.vaadin.teemu.switchui.Switch;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.ControllerUI;
import au.org.noojee.irrigation.dao.PinDao;
import au.org.noojee.irrigation.entities.Pin;
import au.org.noojee.irrigation.types.PinStatus;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureau;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureaus;
import au.org.noojee.irrigation.weather.bureaus.WeatherStation;

public class TouchConfigurationView extends VerticalLayout implements SmartView
{
	private static final long serialVersionUID = 1L;
	public static final String NAME = "TouchConfigurations";

	private ComboBox<WeatherStation> weatherStation;
	private ComboBox<WeatherBureau> weatherBureau;
	private GridLayout pinGrid;

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
		Label headingLabel = new Label("Configuration");
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

		this.addComponent(buildPinGrid());
		this.setExpandRatio(pinGrid, 1);

		Button addButton = new Button("Add", VaadinIcons.PLUS_CIRCLE);
		addButton.setStyleName("i4p-button");
		Responsive.makeResponsive(addButton);

		addButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		addButton.addStyleName(ValoTheme.BUTTON_HUGE);
		this.addComponent(addButton);
		this.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
		addButton.addClickListener(l -> addPin());

	}

	private Component buildPinGrid()
	{
		// Get a list of Already configured pinstouchConfiguration
		PinDao daoPin = new PinDao();
		List<Pin> pins = daoPin.getAll();
		pins = pins.stream().sorted(new Comparator<Pin>()
		{

			@Override
			public int compare(Pin o1, Pin o2)
			{
				return o1.getDeviceName().compareTo(o2.getDeviceName());
			}
		}).collect(Collectors.toList());

		pinGrid = new GridLayout();
		pinGrid.setSpacing(false);
		pinGrid.setMargin(false);

		pinGrid.setSizeFull();
		pinGrid.setColumns(4);

		int row = 0;

		for (Pin pin : pins)
		{

			Label deviceNameLabel = new Label(pin.getDeviceName());
			deviceNameLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(deviceNameLabel);
			pinGrid.addComponent(deviceNameLabel);

			pinGrid.addComponent(createOnOffSwitch(pin));

			Button editButton = new Button("Edit", VaadinIcons.EDIT);
			editButton.setStyleName("i4p-button");
			Responsive.makeResponsive(editButton);

			editButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			editButton.setData(pin);
			editButton.addClickListener(e -> editPin(e));
			// deleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
			pinGrid.addComponent(editButton);

			Button deleteButton = new Button("Delete", VaadinIcons.MINUS_CIRCLE);
			deleteButton.setStyleName("i4p-button");
			Responsive.makeResponsive(deleteButton);

			deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
			deleteButton.setData(pin);
			deleteButton.addClickListener(e -> deletePin(e));
			// deleteButton.addStyleName(ValoTheme.BUTTON_SMALL);
			pinGrid.addComponent(deleteButton);

		}

		return pinGrid;
	}

	private Switch createOnOffSwitch(Pin pin)
	{
		Switch pinToggle = new Switch();
		pinToggle.setStyleName("i4p-switch");
		Responsive.makeResponsive(pinToggle);

		pinToggle.setValue(pin.getCurrentStatus() == PinStatus.ON);

		pinToggle.addValueChangeListener(e ->
			{
				if (e.getValue() == true)
					turnOn(pinToggle, pin);
				else
					turnOff(pinToggle, pin);
			});

		return pinToggle;
	}

	private void turnOff(Switch pinToggle, Pin pin)
	{
		pin.setOff();
	}

	private void turnOn(Switch pinToggle, Pin pin)
	{
		pin.setOn();
	}

	private void deletePin(ClickEvent e)
	{
		Pin pin = (Pin) e.getButton().getData();

		PinDao pinDao = new PinDao();
		pinDao.delete(pin);
		GridLayout originalGrid = pinGrid;
		this.replaceComponent(originalGrid, buildPinGrid());
		// lazy refresh.
		UI.getCurrent().getNavigator().navigateTo(TouchConfigurationView.NAME);
	}

	private void editPin(ClickEvent e)
	{
		Pin pin = (Pin) e.getButton().getData();

		DefinePinView definePinView = (DefinePinView) ((ControllerUI) UI.getCurrent()).getView(DefinePinView.NAME);
		definePinView.setBean(pin);
		UI.getCurrent().getNavigator().navigateTo(DefinePinView.NAME);
	}

	private void addPin()
	{
		DefinePinView definePinView = (DefinePinView) ((ControllerUI) UI.getCurrent()).getView(DefinePinView.NAME);

		definePinView.setBean(null);

		UI.getCurrent().getNavigator().navigateTo(DefinePinView.NAME);

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

}
