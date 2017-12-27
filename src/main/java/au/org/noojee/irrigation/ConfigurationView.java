package au.org.noojee.irrigation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ConfigurationView extends VerticalLayout implements View
{
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Configurations";

	boolean built = false;

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
//
//		Label headlingLabel = new Label("<b>Configuration</b>");
//		headlingLabel.setContentMode(ContentMode.HTML);
//		heading.addComponent(headlingLabel);
//		heading.setComponentAlignment(headlingLabel, Alignment.TOP_CENTER);
//		this.addComponent(heading);

		GridLayout pinGrid = new GridLayout();
	//	pinGrid.setColumnExpandRatio(1, 1);
		//pinGrid.setSpacing(true);
		pinGrid.setSizeFull();
		
		this.addComponent(pinGrid);
	//	this.setExpandRatio(pinGrid, 1);


		int row = 0;


		pinGrid.setColumns(7);
		
		pinGrid.addComponent(new Label("Label"));
		pinGrid.addComponent(new Label("Type"));
		pinGrid.addComponent(new Label("PIN"));
		pinGrid.addComponent(new Label("Switch Type"));
		pinGrid.newLine();

		List<Pin> pins = Arrays.asList(RaspiPin.allPins());
		pins = pins.stream().sorted((l,r) -> l.getAddress() - r.getAddress()).collect(Collectors.toList());
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

	private void turnOff(ClickEvent e)
	{
		final GpioController gpio = GpioFactory.getInstance();

		@SuppressWarnings("unchecked")
		ComboBox<Pin> pinCombo = (ComboBox<Pin>) e.getButton().getData();
		Pin pin = pinCombo.getValue();

		GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(pin);

		//final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(pin);

		gpioPin.setState(PinState.HIGH);
		gpioPin.high();

	}

	private void turnOn(ClickEvent e)
	{
		final GpioController gpio = GpioFactory.getInstance();

		@SuppressWarnings("unchecked")
		ComboBox<Pin> pinCombo = (ComboBox<Pin>) e.getButton().getData();
		Pin pin = pinCombo.getValue();

		GpioPinDigitalOutput gpioPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(pin);

		//final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(pin);

		gpioPin.setState(PinState.LOW);
		gpioPin.low();
	}

}
