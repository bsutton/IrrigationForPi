package au.org.noojee.irrigation.views;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.ControllerUI;
import au.org.noojee.irrigation.dao.UserDao;
import au.org.noojee.irrigation.entities.User;
import au.org.noojee.irrigation.views.editors.UserEditorView;

public class UserView extends VerticalLayout
		implements  SmartView, ViewChangeListener
{
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Users";
	public static final String LABEL = "Users";

	private GridLayout userGrid;

	public UserView()
	{
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
		Label headingLabel = new Label("Users");
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
		addButton.addClickListener(l -> addUser());

	}

	private Component buildGrid()
	{
		// Get a list of Already configured Garden Beds
		UserDao daoUser = new UserDao();
		List<User> users = daoUser.getAll();

		userGrid = new GridLayout();
		userGrid.setMargin(new MarginInfo(false, false, false, true));
		userGrid.setStyleName("i4p-grid");
		userGrid.setSpacing(false);

		userGrid.setWidth("100%");
		userGrid.setHeightUndefined();
		userGrid.setColumns(3);

		for (User user : users)
		{
			Label usernameLabel = new Label(user.getName());
			usernameLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(usernameLabel);
			userGrid.addComponent(usernameLabel);

			Label passwordLabel = new Label(user.getPassword());
			passwordLabel.setStyleName("i4p-label");
			Responsive.makeResponsive(passwordLabel);
			userGrid.addComponent(passwordLabel);

			Button editButton = new Button("Edit", VaadinIcons.EDIT);
			editButton.setStyleName("i4p-button");
			Responsive.makeResponsive(editButton);

			editButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			editButton.setData(user);
			editButton.addClickListener(e -> editUser(e));
			userGrid.addComponent(editButton);
		}

		return userGrid;
	}

	private void editUser(ClickEvent e)
	{
		User user = (User) e.getButton().getData();

		UserEditorView editUserView = (UserEditorView) ((ControllerUI) UI.getCurrent())
				.getView(UserEditorView.NAME);
		editUserView.setBean(user);

		UI.getCurrent().getNavigator().navigateTo(UserEditorView.NAME);

	}
	

	private void addUser()
	{

		UserEditorView userEditorView = (UserEditorView) ((ControllerUI) UI.getCurrent())
				.getView(UserEditorView.NAME);

		userEditorView.setBean(null);

		UI.getCurrent().getNavigator().navigateTo(UserEditorView.NAME);

	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event)
	{
		return true;
	}


}
