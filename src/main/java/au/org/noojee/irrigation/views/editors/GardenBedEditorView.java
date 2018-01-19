package au.org.noojee.irrigation.views.editors;

import java.util.List;

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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.EndPointDao;
import au.org.noojee.irrigation.dao.GardenBedDao;
import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
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
	private CheckBox bleedLineCheckbox;


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

	private void bindFields()
	{
		//binder.bindInstanceFields(this);
		
		binder.forField(this.gardenBedName)
		.asRequired("Please enter a Label for the Garden Bed.")
		.bind(GardenBed::getName, GardenBed::setName);
		
		
		binder.forField(this.valveCombo)
		.asRequired("Please select a Valve")
		.bind(GardenBed::getValve, GardenBed::setValve);
		
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
		
		// we must update the master valve list every time as it can change.
		
		EndPointDao daoEndPoint = new EndPointDao();
		List<EndPoint> valves = daoEndPoint.getAllValves();
		ListDataProvider<EndPoint> valveProvider = new ListDataProvider<EndPoint>(
				valves);
		valveCombo.setDataProvider(valveProvider);

		List<EndPoint> masterValves = daoEndPoint.getMasterValves();
		ListDataProvider<EndPoint> masterValveProvider = new ListDataProvider<EndPoint>(
				masterValves);
		masterValveCombo.setDataProvider(masterValveProvider);

		return this;
	}

	public void setBean(GardenBed gardenBed)
	{
		// make certain the UI is initialised.
		getViewComponent();
		
		if (gardenBed != null)
		{
			this.isEdit = true;
			this.editedGardenBed = gardenBed;

			this.deleteButton.setData(this.editedGardenBed);
			this.deleteButton.setVisible(true);

			this.gardenBedName.setValue(gardenBed.getName());
			this.valveCombo.setValue(gardenBed.getValve());
			
			this.masterValveCombo.setValue(gardenBed.getMasterValve());
			this.bleedLineCheckbox.setValue(gardenBed.isBleedLine());

		}
		else
		{
			this.deleteButton.setVisible(false);
			this.gardenBedName.setValue("");
			
			this.masterValveCombo.setValue(null);
			this.valveCombo.setValue(null);
			this.bleedLineCheckbox.setValue(false);


			this.isEdit = false;
		}

	}

	private Component buildUI()
	{
		this.setSizeFull();

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
		this.valveCombo.setWidth(60.0f, Unit.MM);
		
		valveCombo.setItemCaptionGenerator(EndPoint::getEndPointName);
		valveCombo.setEmptySelectionAllowed(false);
		valveCombo.setTextInputAllowed(false);
	
		
		this.masterValveCombo = new ComboBox<>("Master Valve");
		this.addComponent(masterValveCombo);
		
		this.masterValveCombo.setItemCaptionGenerator(EndPoint::getEndPointName);
		this.masterValveCombo.setEmptySelectionAllowed(true);
		this.masterValveCombo.setTextInputAllowed(false);
		this.masterValveCombo.addValueChangeListener(e -> masterValveSelected(e));
		this.masterValveCombo.setWidth(60.0f, Unit.MM);
		
		
		bleedLineCheckbox = new CheckBox("Bleed line (Recommended)");
		this.addComponent(bleedLineCheckbox);
			

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

		gardenBedName.focus();

		return this;
	}

	
	private void masterValveSelected(ValueChangeEvent<EndPoint> e)
	{
		if (e.getValue() == null)
		{
			bleedLineCheckbox.setVisible(false);
		}
		else
		{
			bleedLineCheckbox.setVisible(true);
			
		}
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

			if (this.isEdit)
				gardenBed = this.editedGardenBed;
			else
				gardenBed = new GardenBed();

			gardenBed.setName(this.gardenBedName.getValue());
			gardenBed.setValve(this.valveCombo.getValue());
			gardenBed.setMasterValve(this.masterValveCombo.getValue());
			gardenBed.setBleedLine(this.bleedLineCheckbox.getValue());

			if (this.isEdit)
				daoGardenBed.merge(gardenBed);
			else
				daoGardenBed.persist(gardenBed);
			UI.getCurrent().getNavigator().navigateTo(GardenBedConfigurationView.NAME);
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
