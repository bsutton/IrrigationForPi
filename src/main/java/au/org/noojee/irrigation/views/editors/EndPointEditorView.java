package au.org.noojee.irrigation.views.editors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.data.Binder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.types.EndPointType;
import au.org.noojee.irrigation.types.PinActivationType;
import au.org.noojee.irrigation.views.EndPointConfigurationView;
import au.org.noojee.irrigation.views.SmartView;

public class EndPointEditorView extends VerticalLayout implements SmartView
{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "EndPointEditor";
	private boolean uiBuilt = false;
	private TextField endPointName;
	private ComboBox<EndPointType> endPointType;
	private ComboBox<PinActivationType> activationType;
	private ComboBox<com.pi4j.io.gpio.Pin> piPinComboBox;

	private Binder<EndPoint> binder = new Binder<>(EndPoint.class);
	private boolean isEdit = false;
	private EndPoint editedEndPoint;
	private Button deleteButton;

	public EndPointEditorView()
	{
	}
	

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		endPointName.focus();
	}


	@Override
	public Component getViewComponent()
	{
		if (!uiBuilt)
		{
			buildUI();
			bindFields();
			this.uiBuilt = true;
		}


		return this;
	}

	private void bindFields()
	{
		binder.bindInstanceFields(this);
		
		binder.forField(this.endPointName)
		.asRequired("Please enter a Label for this End Point.")
		.bind(EndPoint::getEndPointName, EndPoint::setEndPointName);
		
		
		binder.forField(this.endPointType)
		.asRequired("Please select a End Point Type")
		.bind(EndPoint::getEndPointType, EndPoint::setEndPointType);
		
		binder.forField(this.piPinComboBox)
		.asRequired("Please select a pin")
		.bind(EndPoint::getPiPin, EndPoint::setPiPin);
		
		binder.forField(this.activationType)
		.asRequired("Please set the Activation Type")
		.bind(EndPoint::getPinActiviationType, EndPoint::setPinActiviationType);
		
		
		
	}


	public void setBean(EndPoint endPoint)
	{
		// make certain the UI is initialised.
		getViewComponent();
		
		if (endPoint != null)
		{
			this.isEdit = true;
			this.editedEndPoint = endPoint;

			this.deleteButton.setData(this.editedEndPoint);
			this.deleteButton.setVisible(true);

			this.endPointName.setValue(endPoint.getEndPointName());
			this.endPointType.setValue(endPoint.getEndPointType());
			this.activationType.setValue(endPoint.getPinActiviationType());
			this.piPinComboBox.setValue(endPoint.getPiPin());
			

		}
		else
		{
			this.deleteButton.setVisible(false);
			this.endPointName.setValue("");
			this.endPointType.setValue(EndPointType.Valve);
			this.activationType.setValue(PinActivationType.HIGH_IS_ON);
			this.piPinComboBox.setSelectedItem(null);
			

			this.isEdit = false;
		}

	}

	private Component buildUI()
	{
		this.setSizeFull();

		HorizontalLayout topLine = new HorizontalLayout();
		this.addComponent(topLine);
		topLine.setWidth("100%");
		
		endPointName = new TextField("Label");
		topLine.addComponent(endPointName);
		topLine.setComponentAlignment(endPointName, Alignment.MIDDLE_LEFT);
		endPointName.setWidth("100%");
		endPointName.setStyleName("i4p-label");
		Responsive.makeResponsive(endPointName);
		
		deleteButton = new Button("Delete", VaadinIcons.MINUS_CIRCLE);
		topLine.addComponent(deleteButton);
		topLine.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
		deleteButton.setStyleName("i4p-button");
		Responsive.makeResponsive(deleteButton);

		deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
		deleteButton.addClickListener(e -> deleteEndPoint(e));



		endPointType = new ComboBox<>("Type");
		this.addComponent(endPointType);
		endPointType.setDataProvider(new ListDataProvider<EndPointType>(Arrays.asList(EndPointType.values())));
		endPointType.setEmptySelectionAllowed(false);
		endPointType.setTextInputAllowed(false);
		

		List<com.pi4j.io.gpio.Pin> gpioPins = Arrays.asList(RaspiPin.allPins());
		gpioPins = gpioPins.stream().sorted((l, r) -> l.getAddress() - r.getAddress()).collect(Collectors.toList());

		piPinComboBox = new ComboBox<>("Pin");
		this.addComponent(piPinComboBox);
		piPinComboBox.setDataProvider(new ListDataProvider<com.pi4j.io.gpio.Pin>(gpioPins));
		piPinComboBox.setTextInputAllowed(false);
		piPinComboBox.setEmptySelectionAllowed(false);

		activationType = new ComboBox<>("Activation");
		this.addComponent(activationType);
		ListDataProvider<PinActivationType> provider = new ListDataProvider<PinActivationType>(
				Arrays.asList(PinActivationType.values()));
		activationType.setDataProvider(provider);
		activationType.setEmptySelectionAllowed(false);
		activationType.setTextInputAllowed(false);
		
		
		VerticalLayout spacer = new VerticalLayout();
		this.addComponent(spacer);
		spacer.setSizeFull();
		this.setExpandRatio(spacer, 1);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidth("100%");
		buttons.setMargin(false);
		this.addComponent(buttons);

		Button btnCancel = new Button("Cancel");
		buttons.addComponent(btnCancel);
		btnCancel.setStyleName("i4p-button");
		Responsive.makeResponsive(btnCancel);
		btnCancel.addClickListener(l -> cancel());
		buttons.setComponentAlignment(btnCancel, Alignment.BOTTOM_LEFT);

		Button btnSave = new Button("Save");
		btnSave.setStyleName("i4p-button");
		Responsive.makeResponsive(btnSave);
		btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnSave.setClickShortcut(KeyCode.ENTER);
		buttons.addComponent(btnSave);
		buttons.setComponentAlignment(btnSave, Alignment.BOTTOM_RIGHT);
		btnSave.addClickListener(l -> save());

		endPointName.focus();

		return this;
	}


	private void deleteEndPoint(ClickEvent e)
	{
		EndPoint endPoint = (EndPoint) e.getButton().getData();

		EndPointDao endPointDao = new EndPointDao();
		endPointDao.delete(endPoint);
		UI.getCurrent().getNavigator().navigateTo(EndPointConfigurationView.NAME);
	}

	private void cancel()
	{
		UI.getCurrent().getNavigator().navigateTo(EndPointConfigurationView.NAME);
	}

	private void save()
	{
		if (binder.validate().isOk())
		{
			EndPointDao daoEndPoint = new EndPointDao();

			EndPoint endPoint;

			if (this.isEdit)
				endPoint = this.editedEndPoint;
			else
				endPoint = new EndPoint();

			endPoint.setEndPointName(this.endPointName.getValue());
			endPoint.setEndPointType(this.endPointType.getValue());
			endPoint.setPinActiviationType(this.activationType.getValue());
			endPoint.setPiPin(this.piPinComboBox.getValue());
			if (this.isEdit)
				daoEndPoint.merge(endPoint);
			else
				daoEndPoint.persist(endPoint);
			UI.getCurrent().getNavigator().navigateTo(EndPointConfigurationView.NAME);
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
