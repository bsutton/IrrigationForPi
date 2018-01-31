package au.org.noojee.irrigation.views.editors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.controllers.GardenBedController;
import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.dao.LightingDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.Lighting;
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
	private CheckBox drainLineCheckbox;

	private Binder<EndPoint> binder = new Binder<>(EndPoint.class);
	private boolean isEdit = false;
	private EndPoint editedEndPoint;
	private EndPointType originalEndPointType;
	private Button deleteButton;
	

	public EndPointEditorView()
	{
		buildUI();
		bindFields();

	}
	

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		endPointName.focus();
	}


	private void bindFields()
	{
		binder.bindInstanceFields(this);

		binder.forField(this.endPointName)
				.asRequired("Please enter a Name for this End Point.")
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
			this.originalEndPointType = endPoint.getEndPointType();

			this.deleteButton.setData(this.editedEndPoint);
			this.deleteButton.setVisible(true);

			this.endPointName.setValue(endPoint.getEndPointName());
			this.endPointType.setValue(endPoint.getEndPointType());
			this.activationType.setValue(endPoint.getPinActiviationType());
			this.piPinComboBox.setValue(endPoint.getPiPin());

			this.drainLineCheckbox.setValue(endPoint.isDrainingLine());

		}
		else
		{
			this.deleteButton.setVisible(false);
			this.endPointName.setValue("");
			this.endPointType.setValue(EndPointType.Valve);
			this.activationType.setValue(PinActivationType.HIGH_IS_ON);
			this.piPinComboBox.setSelectedItem(null);
			this.drainLineCheckbox.setValue(false);

			this.editedEndPoint = null;
			this.originalEndPointType = null;

			this.isEdit = false;
		}

	}

	private Component buildUI()
	{
		this.setSizeFull();

		Label headingLabel = new Label("End Point");
		headingLabel.setStyleName("i4p-heading");
		Responsive.makeResponsive(headingLabel);
		this.addComponent(headingLabel);
		this.setComponentAlignment(headingLabel, Alignment.TOP_CENTER);

		HorizontalLayout topLine = new HorizontalLayout();
		this.addComponent(topLine);
		topLine.setWidth("100%");

		endPointName = new TextField("Name");
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
		endPointType.addValueChangeListener(e -> masterValveSelected(e));

		drainLineCheckbox = new CheckBox("Drain line (Recommended)");
		this.addComponent(drainLineCheckbox);
		drainLineCheckbox.setVisible(false);

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

		lightSwitchDeleteProcessing(endPoint);

		EndPointDao endPointDao = new EndPointDao();
		endPointDao.delete(endPoint);

		UI.getCurrent().getNavigator().navigateTo(EndPointConfigurationView.NAME);
	}

	private void masterValveSelected(ValueChangeEvent<EndPointType> e)
	{
		if (e.getValue() == EndPointType.MasterValve)
		{
			drainLineCheckbox.setVisible(true);

		}
		else
		{
			drainLineCheckbox.setVisible(false);

		}
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

			Pin piPin = this.piPinComboBox.getValue();
			if (isPinInUse(piPin))
				Notification.show("Pin in Use",
						"The selected Pin is already used by End Point '" + getPinUsedBy(piPin).getEndPointName()
								+ "'.",
						Type.ERROR_MESSAGE);
			else
			{

				if (this.isEdit)
					endPoint = this.editedEndPoint;
				else
					endPoint = new EndPoint();

				endPoint.setEndPointName(this.endPointName.getValue());
				endPoint.setEndPointType(this.endPointType.getValue());
				endPoint.setDrainLine(this.drainLineCheckbox.getValue());
				endPoint.setPinActiviationType(this.activationType.getValue());
				endPoint.setPiPin(piPin);
				try
				{
					if (this.isEdit)
						daoEndPoint.merge(endPoint);
					else
						daoEndPoint.persist(endPoint);
				}
				catch (RollbackException e)
				{
					int error = 0;
					Throwable cause = e.getCause();
					if (cause instanceof DatabaseException)
					{
						DatabaseException dbexception = (DatabaseException) cause;
						error = dbexception.getErrorCode();
					}
					switch (error)
					{
						case 0:
							Notification.show("Save failed.", e.getMessage(), Type.ERROR_MESSAGE);
							break;
						case 4002:
							Notification.show("Save failed.", "The End Point name entered is already used.",
									Type.ERROR_MESSAGE);
							break;
					}
					return;

				}

				lightSwichEditProcessing(endPoint);

				// re-initialise the valve controller now we have changed a valve
				GardenBedController.init();

				UI.getCurrent().getNavigator().navigateTo(EndPointConfigurationView.NAME);
			}
		}
	}

	/**
	 * We are doing some hacky stuff here to avoid the end user having to add/delete light entities.
	 */
	private void lightSwitchDeleteProcessing(EndPoint endPoint)
	{
		LightingDao daoLighting = new LightingDao();

		daoLighting.deleteByEndPoint(endPoint);

	}

	private void lightSwichEditProcessing(EndPoint endPoint)
	{
		if (isEdit)
		{
			// need to determine if there has been a change of type.

			if (endPoint.getEndPointType() != this.originalEndPointType)
			{
				// so the type has change so we are going to have do some db editing.

				// If the original was a light then we are no longer a light so delete it.
				if (this.originalEndPointType == EndPointType.Light)
					lightSwitchDeleteProcessing(endPoint);

				// If the new end point is a light then we need to create one.
				if (endPoint.getEndPointType() == EndPointType.Light)
					lightSwitchCreateProcessing(endPoint);

			}
		}
		else
		{
			// So not an edit then we may have a new light.
			lightSwitchCreateProcessing(endPoint);
		}

	}

	private void lightSwitchCreateProcessing(EndPoint endPoint)
	{
		if (endPoint.getEndPointType() == EndPointType.Light)
		{
			LightingDao daoLighting = new LightingDao();

			Lighting lighting = new Lighting(endPoint);

			daoLighting.persist(lighting);
		}

	}

	private EndPoint getPinUsedBy(Pin piPin)
	{
		EndPoint usedBy = null;
		EndPointDao daoEndPoint = new EndPointDao();

		List<EndPoint> usedByList = daoEndPoint.getByPin(piPin);

		if (this.isEdit)
			usedByList = usedByList.stream().filter(e -> !e.equals(this.editedEndPoint)).collect(Collectors.toList());

		if (usedByList.size() != 0)
			usedBy = usedByList.get(0);

		return usedBy;
	}

	private boolean isPinInUse(Pin piPin)
	{
		boolean inUse = false;

		EndPointDao daoEndPoint = new EndPointDao();

		List<EndPoint> usedByList = daoEndPoint.getByPin(piPin);

		// true if we have at least one element that isn't the currently edited endpoint.
		if (this.isEdit)
			inUse = usedByList.stream().anyMatch(e -> !e.equals(this.editedEndPoint));
		else
			inUse = usedByList.size() != 0;
		return inUse;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
