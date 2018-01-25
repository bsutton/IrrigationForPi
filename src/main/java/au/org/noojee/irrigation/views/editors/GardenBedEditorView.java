package au.org.noojee.irrigation.views.editors;

import java.util.List;
import java.util.stream.Collectors;

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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.types.ValveController;
import au.org.noojee.irrigation.views.GardenBedConfigurationView;
import au.org.noojee.irrigation.views.SmartView;

public class GardenBedEditorView extends VerticalLayout implements SmartView
{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "GardenBedEditor";
	private boolean uiBuilt = false;
	private TextField gardenBedName;
	private ComboBox<EndPoint> valveCombo;
	private ComboBox<EndPoint> masterValveCombo;

	private Binder<GardenBed> binder = new Binder<>(GardenBed.class);
	private boolean isEdit = false;
	private GardenBed editedGardenBed;
	private Button deleteButton;

	public GardenBedEditorView()
	{
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		gardenBedName.focus();
	}


	@Override
	public Component getViewComponent()
	{
		buildUI();

		// we must update the master valve list every time as it can change.

		EndPointDao daoEndPoint = new EndPointDao();
		List<EndPoint> valves = daoEndPoint.getAllValves();
		ListDataProvider<EndPoint> valveProvider = new ListDataProvider<EndPoint>(
				valves);
		valveCombo.setDataProvider(valveProvider);

		if (this.isEdit)
			this.valveCombo.setValue(this.editedGardenBed.getValve());

		List<EndPoint> masterValves = daoEndPoint.getMasterValves();
		ListDataProvider<EndPoint> masterValveProvider = new ListDataProvider<EndPoint>(
				masterValves);
		masterValveCombo.setDataProvider(masterValveProvider);

		if (this.isEdit)
			this.masterValveCombo.setValue(this.editedGardenBed.getMasterValve());

		return this;
	}

	public void setBean(GardenBed gardenBed)
	{
		// make certain the UI is initialised.
		buildUI();


		if (gardenBed != null)
		{
			this.isEdit = true;
			this.editedGardenBed = gardenBed;

			this.deleteButton.setData(this.editedGardenBed);
			this.deleteButton.setVisible(true);

			this.gardenBedName.setValue(gardenBed.getName());
			this.valveCombo.setValue(gardenBed.getValve());
			this.masterValveCombo.setValue(gardenBed.getMasterValve());
		}
		else
		{
			this.isEdit = false;
			this.editedGardenBed = null;
			
			this.deleteButton.setData(null);
			this.deleteButton.setVisible(false);

			this.gardenBedName.setValue("");
			this.valveCombo.setValue(null);
			this.masterValveCombo.setValue(null);
		}

	}

	private void buildUI()
	{
		if (!uiBuilt)
		{
			this.setSizeFull();

			Label headingLabel = new Label("Garden Bed");
			headingLabel.setStyleName("i4p-heading");
			Responsive.makeResponsive(headingLabel);
			this.addComponent(headingLabel);
			this.setComponentAlignment(headingLabel, Alignment.TOP_CENTER);

			HorizontalLayout topLine = new HorizontalLayout();
			this.addComponent(topLine);
			topLine.setWidth("100%");

			gardenBedName = new TextField("Label");
			topLine.addComponent(gardenBedName);
			topLine.setComponentAlignment(gardenBedName, Alignment.MIDDLE_LEFT);
			gardenBedName.setWidth("100%");
			gardenBedName.setStyleName("i4p-label");
			Responsive.makeResponsive(gardenBedName);

			deleteButton = new Button("Delete", VaadinIcons.MINUS_CIRCLE);
			topLine.addComponent(deleteButton);
			topLine.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
			deleteButton.setStyleName("i4p-button");
			Responsive.makeResponsive(deleteButton);

			deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
			deleteButton.addClickListener(e -> deleteGardenBed(e));

			this.valveCombo = new ComboBox<>("Valve");
			this.addComponent(valveCombo);

			this.valveCombo.addValueChangeListener(l ->
				{
					System.out.println("valve changed to " + l.getValue());
				});

			this.valveCombo.setItemCaptionGenerator(EndPoint::getEndPointName);
			this.valveCombo.setEmptySelectionAllowed(false);
			this.valveCombo.setTextInputAllowed(false);
			this.valveCombo.setWidth(60.0f, Unit.MM);

			this.masterValveCombo = new ComboBox<>("Master Valve");
			this.addComponent(masterValveCombo);

			this.masterValveCombo.setItemCaptionGenerator(EndPoint::getEndPointName);
			this.masterValveCombo.setEmptySelectionAllowed(true);
			this.masterValveCombo.setTextInputAllowed(false);
			this.masterValveCombo.setWidth(60.0f, Unit.MM);

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
			
			
			bindFields();

			this.uiBuilt = true;
		}

	}

	private void bindFields()
	{
		// binder.bindInstanceFields(this);

		binder.forField(this.gardenBedName)
				.asRequired("Please enter a Label for the Garden Bed.")
				.bind(GardenBed::getName, GardenBed::setName);

		binder.forField(this.valveCombo)
				.asRequired("Please select a Valve")
				.bind(GardenBed::getValve, GardenBed::setValve);

	}

	private void deleteGardenBed(ClickEvent e)
	{
		GardenBed gardenBed = (GardenBed) e.getButton().getData();

		GardenBedDao daoGardenBed = new GardenBedDao();
		daoGardenBed.delete(gardenBed);
		UI.getCurrent().getNavigator().navigateTo(GardenBedConfigurationView.NAME);
	}

	private void cancel()
	{
		UI.getCurrent().getNavigator().navigateTo(GardenBedConfigurationView.NAME);
	}

	private void save()
	{
		if (binder.validate().isOk())
		{
			GardenBedDao daoGardenBed = new GardenBedDao();

			GardenBed gardenBed;

			EndPoint valve = this.valveCombo.getValue();

			if (isEndPointInUse(valve))
				Notification.show("Pin in Use",
						"The selected Pin is already used by Garden Bed '" + getEndPointUsedBy(valve).getName() + "'.",
						Type.ERROR_MESSAGE);
			else
			{

				if (this.isEdit)
					gardenBed = this.editedGardenBed;
				else
					gardenBed = new GardenBed();

				gardenBed.setName(this.gardenBedName.getValue());
				gardenBed.setValve(this.valveCombo.getValue());
				gardenBed.setMasterValve(this.masterValveCombo.getValue());

				if (this.isEdit)
					daoGardenBed.merge(gardenBed);
				else
					daoGardenBed.persist(gardenBed);

				// re-initialise the valve controller now we have changed a garden bed
				ValveController.init();

				UI.getCurrent().getNavigator().navigateTo(GardenBedConfigurationView.NAME);
			}
		}
	}

	private GardenBed getEndPointUsedBy(EndPoint valve)
	{
		GardenBed usedBy = null;
		GardenBedDao daoGardenBed = new GardenBedDao();

		List<GardenBed> usedByList = daoGardenBed.getByValve(valve);

		if (this.isEdit)
			usedByList = usedByList.stream().filter(e -> !e.equals(this.editedGardenBed)).collect(Collectors.toList());

		if (usedByList.size() != 0)
			usedBy = usedByList.get(0);

		return usedBy;
	}

	private boolean isEndPointInUse(EndPoint valve)
	{
		boolean inUse = false;
		GardenBedDao daoGardenBed = new GardenBedDao();

		List<GardenBed> usedByList = daoGardenBed.getByValve(valve);

		// true if we have at least one element that isn't the currently edited garden bed.
		if (this.isEdit)
			inUse = usedByList.stream().anyMatch(e -> !e.equals(this.editedGardenBed));
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
