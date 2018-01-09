package au.org.noojee.irrigation.views;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.data.Binder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.PinDao;
import au.org.noojee.irrigation.entities.Pin;
import au.org.noojee.irrigation.types.EndPointType;
import au.org.noojee.irrigation.types.PinActivationType;

public class DefinePinView extends VerticalLayout implements SmartView
{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "DefinePin";
	private boolean uiBuilt = false;
	private TextField deviceName;
	private ComboBox<EndPointType> endPointType;
	private ComboBox<PinActivationType> activationType;
	private ComboBox<com.pi4j.io.gpio.Pin> piPin;
	private Label statusLabel = new Label();

	private Binder<Pin> binder = new Binder<>(Pin.class);
	private boolean isEdit = false;
	private Pin editedPin;

	public DefinePinView()
	{
		binder.setStatusLabel(statusLabel);
	}
	

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		deviceName.focus();
	}


	@Override
	public Component getViewComponent()
	{
		if (!uiBuilt)
		{
			buildUI();
			this.uiBuilt = true;
		}
		return this;
	}

	void setBean(Pin pin)
	{
		// make certain the UI is initialised.
		getViewComponent();
		
		if (pin != null)
		{
			this.isEdit = true;
			this.editedPin = pin;

			this.deviceName.setValue(pin.getDeviceName());
			this.endPointType.setValue(pin.getEndPointType());
			this.activationType.setValue(pin.getPinActiviationType());
			this.piPin.setValue(pin.getPiPin());
		}
		else
		{
			this.deviceName.setValue("");
			this.endPointType.setValue(EndPointType.Irrigation);
			this.activationType.setValue(PinActivationType.HIGH_IS_ON);
			this.piPin.setSelectedItem(null);

			this.isEdit = false;
		}

	}

	private Component buildUI()
	{
		this.setSizeFull();

		deviceName = new TextField("Label");
		deviceName.setWidth("100%");
		this.addComponent(deviceName);

		List<com.pi4j.io.gpio.Pin> gpioPins = Arrays.asList(RaspiPin.allPins());
		gpioPins = gpioPins.stream().sorted((l, r) -> l.getAddress() - r.getAddress()).collect(Collectors.toList());

		endPointType = new ComboBox<>("Type");
		endPointType.setDataProvider(new ListDataProvider<EndPointType>(Arrays.asList(EndPointType.values())));
		endPointType.setEmptySelectionAllowed(false);
		endPointType.setTextInputAllowed(false);
		this.addComponent(endPointType);

		piPin = new ComboBox<>("Pin");
		piPin.setDataProvider(new ListDataProvider<com.pi4j.io.gpio.Pin>(gpioPins));
		this.addComponent(piPin);

		activationType = new ComboBox<>("Activation");
		ListDataProvider<PinActivationType> provider = new ListDataProvider<PinActivationType>(
				Arrays.asList(PinActivationType.values()));
		activationType.setDataProvider(provider);
		activationType.setEmptySelectionAllowed(false);
		activationType.setTextInputAllowed(false);
		this.addComponent(activationType);

		VerticalLayout spacer = new VerticalLayout();
		this.addComponent(spacer);
		spacer.setSizeFull();
		this.setExpandRatio(spacer, 1);

		this.addComponent(statusLabel);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidth("100%");
		buttons.setMargin(false);
		this.addComponent(buttons);

		Button btnCancel = new Button("Cancel");
		buttons.addComponent(btnCancel);
		btnCancel.addClickListener(l -> cancel());
		buttons.setComponentAlignment(btnCancel, Alignment.BOTTOM_LEFT);

		Button btnSave = new Button("Save");
		btnSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btnSave.setClickShortcut(KeyCode.ENTER);
		buttons.addComponent(btnSave);
		buttons.setComponentAlignment(btnSave, Alignment.BOTTOM_RIGHT);
		btnSave.addClickListener(l -> save());

		deviceName.focus();

		binder.bindInstanceFields(this);
		// binder.bind(deviceName, propertyName)

		return this;
	}

	private void cancel()
	{
		UI.getCurrent().getNavigator().navigateTo(TouchConfigurationView.NAME);
	}

	private void save()
	{
		if (binder.validate().isOk())
		{
			PinDao daoPin = new PinDao();

			Pin pin;

			if (this.isEdit)
				pin = this.editedPin;
			else
				pin = new Pin();

			pin.setDeviceName(this.deviceName.getValue());
			pin.setEndPointType(this.endPointType.getValue());
			pin.setPinActiviationType(this.activationType.getValue());
			pin.setPiPin(this.piPin.getValue());
			if (this.isEdit)
				daoPin.merge(pin);
			else
				daoPin.persist(pin);
			UI.getCurrent().getNavigator().navigateTo(TouchConfigurationView.NAME);
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
