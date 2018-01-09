package au.org.noojee.irrigation.views;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.types.EndPointType;
import au.org.noojee.irrigation.types.PinActivationType;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureau;
import au.org.noojee.irrigation.weather.bureaus.WeatherBureaus;
import au.org.noojee.irrigation.weather.bureaus.WeatherStation;

public class ConfigurationView extends VerticalLayout implements SmartView
{
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Configurations";

	boolean built = false;
	private ComboBox<WeatherStation> weatherStation;
	private ComboBox<WeatherBureau> weatherBureau;

	public void enter(ViewChangeEvent event)
	{

		if (!built)
		{
			build();
			built = true;
		}
	}

	void build()
	{
		this.setSizeFull();

		HorizontalLayout heading = new HorizontalLayout();
		heading.setWidth("100%");

		Label headlingLabel = new Label("<b>Configuration</b>");
		headlingLabel.setContentMode(ContentMode.HTML);
		heading.addComponent(headlingLabel);
		heading.setComponentAlignment(headlingLabel, Alignment.TOP_CENTER);
		this.addComponent(heading);

		weatherBureau = new ComboBox<>("Weather Bureau");
		this.addComponent(weatherBureau);
		weatherBureau.setDataProvider(new ListDataProvider<>(WeatherBureaus.getBureaus()));
		weatherBureau.setItemCaptionGenerator(WeatherBureau::getCountryName);
		weatherBureau.addValueChangeListener(l -> buerauSelected(l));

		weatherStation = new ComboBox<>("Weather Station");
		this.addComponent(weatherBureau);

		GridLayout pinGrid = new GridLayout();
		// pinGrid.setColumnExpandRatio(1, 1);
		// pinGrid.setSpacing(true);
		pinGrid.setSizeFull();

		this.addComponent(pinGrid);
		// this.setExpandRatio(pinGrid, 1);

		int row = 0;

		pinGrid.setColumns(7);

		pinGrid.addComponent(new Label("Label"));
		pinGrid.addComponent(new Label("Type"));
		pinGrid.addComponent(new Label("PIN"));
		pinGrid.addComponent(new Label("Switch Type"));
		pinGrid.newLine();

		List<Pin> pins = Arrays.asList(RaspiPin.allPins());
		pins = pins.stream().sorted((l, r) -> l.getAddress() - r.getAddress()).collect(Collectors.toList());
		for (int pinRow = 0; pinRow < pins.size(); pinRow++)
		{
			pinGrid.insertRow(row++);
			TextField label = new TextField();
			label.setWidth("100%");
			pinGrid.addComponent(label);

			ComboBox<EndPointType> device = new ComboBox<>();
			device.setDataProvider(new ListDataProvider<EndPointType>(Arrays.asList(EndPointType.values())));
			pinGrid.addComponent(device);

			ComboBox<Pin> pin = new ComboBox<>();
			pin.setDataProvider(new ListDataProvider<Pin>(pins));
			pinGrid.addComponent(pin);

			ComboBox<PinActivationType> pinTypes = new ComboBox<>();
			ListDataProvider<PinActivationType> provider = new ListDataProvider<PinActivationType>(
					Arrays.asList(PinActivationType.values()));
			pinTypes.setDataProvider(provider);

			pinGrid.addComponent(pinTypes);

			Button on = new Button("On");
			on.setData(pin);
			on.addClickListener(e -> turnOn(e));
			pinGrid.addComponent(on);

			Button off = new Button("Off");
			off.setData(pin);
			off.addClickListener(e -> turnOff(e));
			pinGrid.addComponent(new Button("Off"));

			pinGrid.newLine();

		}

	}

	private void buerauSelected(ValueChangeEvent<WeatherBureau> l)
	{
		WeatherBureau bureau = l.getValue();

		this.weatherStation.setDataProvider(new ListDataProvider<>(bureau.getStations()));
	}

	private void turnOff(ClickEvent e)
	{
		final GpioController gpio = GpioFactory.getInstance();

		@SuppressWarnings("unchecked")
		ComboBox<Pin> pinCombo = (ComboBox<Pin>) e.getButton().getData();
		Pin pin = pinCombo.getValue();
		if (pin == null)
			Notification.show("You need to select a PIN first");
		else
		{
			GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(pin);
			gpioPin.high();
		}

	}

	private void turnOn(ClickEvent e)
	{
		final GpioController gpio = GpioFactory.getInstance();

		@SuppressWarnings("unchecked")
		ComboBox<Pin> pinCombo = (ComboBox<Pin>) e.getButton().getData();
		Pin pin = pinCombo.getValue();
		if (pin == null)
			Notification.show("You need to select a PIN first");
		else
		{
			GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(pin);
			gpioPin.low();
		}
	}
	
	@Override
	public String getName()
	{
		return NAME;
	}



}
