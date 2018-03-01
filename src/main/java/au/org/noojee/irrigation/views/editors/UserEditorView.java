package au.org.noojee.irrigation.views.editors;

import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.UserDao;
import au.org.noojee.irrigation.entities.User;
import au.org.noojee.irrigation.views.SmartView;
import au.org.noojee.irrigation.views.UserView;

public class UserEditorView extends VerticalLayout implements SmartView
{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "UserEditor";
	private boolean uiBuilt = false;

	private Binder<User> binder = new Binder<>(User.class);
	private boolean isEdit = false;
	private User editedUser;
	private Button deleteButton;

	private TextField usernameField;
	private PasswordField passwordField;
	private CheckBox administratorCheckBox;
	private TextArea descriptionField;

	public UserEditorView()
	{
		buildUI();
		usernameField.focus();

	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		usernameField.focus();
	}

	public void setBean(User user)
	{
		// make certain the UI is initialised.
		buildUI();

		if (user != null)
		{
			this.isEdit = true;
			this.editedUser = user;

			this.deleteButton.setData(this.editedUser);
			this.deleteButton.setVisible(true);

			this.usernameField.setValue(user.getName());
			this.passwordField.setValue(user.getPassword());
			this.descriptionField.setValue(user.getDesription());
			this.administratorCheckBox.setValue(user.isAdministrator());
		}
		else
		{
			this.isEdit = false;
			this.editedUser = null;

			this.deleteButton.setData(null);
			this.deleteButton.setVisible(false);

			this.usernameField.setValue(" ");
			this.passwordField.setValue(" ");
			this.descriptionField.setValue("");
			this.administratorCheckBox.setValue(false);
		}

	}

	private void buildUI()
	{
		if (!uiBuilt)
		{
			this.setSizeFull();

			Label headingLabel = new Label("User");
			headingLabel.setStyleName("i4p-heading");
			Responsive.makeResponsive(headingLabel);
			this.addComponent(headingLabel);
			this.setComponentAlignment(headingLabel, Alignment.TOP_CENTER);

			HorizontalLayout topLine = new HorizontalLayout();
			this.addComponent(topLine);
			topLine.setWidth("100%");

			usernameField = new TextField("User");
			this.addComponent(usernameField);
			this.setComponentAlignment(usernameField, Alignment.MIDDLE_LEFT);
			usernameField.setWidth("100%");
			usernameField.setStyleName("i4p-label");
			usernameField.addStyleName("username");
			Responsive.makeResponsive(usernameField);
			JavaScript.getCurrent().execute(
				    "debugger; document.getElementsByClassName('v-textfield')[0].setAttribute('autocomplete', 'new-password')");
			

			passwordField = new PasswordField("Password");
			this.addComponent(passwordField);
			this.setComponentAlignment(passwordField, Alignment.MIDDLE_LEFT);
			passwordField.setWidth("100%");
			passwordField.setStyleName("i4p-label");
			passwordField.addStyleName("password");
			
			Responsive.makeResponsive(passwordField);
			
//			TextField tf = new TextField();
//			tf.addStyleName("xyz");
			JavaScript.getCurrent().execute(
			    "document.getElementsByClassName('v-textfield')[1].setAttribute('autocomplete', 'new-password')");
			

			descriptionField = new TextArea("Description");
			this.addComponent(descriptionField);
			this.setComponentAlignment(descriptionField, Alignment.MIDDLE_LEFT);
			descriptionField.setWidth("100%");
			descriptionField.setStyleName("i4p-label");
			Responsive.makeResponsive(descriptionField);

			administratorCheckBox = new CheckBox("Administrator");
			this.addComponent(administratorCheckBox);
			administratorCheckBox.setVisible(false);
			administratorCheckBox.setStyleName("i4p-label");
			Responsive.makeResponsive(administratorCheckBox);

			deleteButton = new Button("Delete", VaadinIcons.MINUS_CIRCLE);
			topLine.addComponent(deleteButton);
			topLine.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
			deleteButton.setStyleName("i4p-button");
			Responsive.makeResponsive(deleteButton);

			deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
			deleteButton.addClickListener(e -> deleteUser(e));

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

		binder.forField(this.usernameField)
				.asRequired("Please enter a Username for the User.")
				.bind(User::getName, User::setName);

		binder.forField(this.passwordField)
				.asRequired("Please enter a Password for the User.")
				.bind(User::getPassword, User::setPassword);

		binder.forField(this.passwordField)
				.bind(User::getDesription, User::setDesription);
		
		binder.forField(this.administratorCheckBox)
		.bind(User::isAdministrator, User::setAdministrator);


	}

	private void deleteUser(ClickEvent e)
	{
		UserDao daoUser = new UserDao();
		daoUser.delete(this.editedUser);
		UI.getCurrent().getNavigator().navigateTo(UserView.NAME);
	}

	private void cancel()
	{
		UI.getCurrent().getNavigator().navigateTo(UserView.NAME);
	}

	private void save()
	{
		if (binder.validate().isOk())
		{
			UserDao daoUser = new UserDao();

			User user;

			String username = usernameField.getValue().trim();
			if (isUsernameInUse(username))
				Notification.show("Username already used",
						"The selected Username already exists: '" + username + "'.",
						Type.ERROR_MESSAGE);
			else
			{

				if (this.isEdit)
					user = this.editedUser;
				else
					user = new User();

				user.setName(this.usernameField.getValue().trim());
				user.setPassword(this.passwordField.getValue().trim());
				user.setDesription(this.descriptionField.getValue().trim());
				user.setAdministrator(this.administratorCheckBox.getValue());

				try
				{

					if (this.isEdit)
						daoUser.merge(user);
					else
						daoUser.persist(user);
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
							Notification.show("Save failed.", "The Garden Bed name entered is already used.",
									Type.ERROR_MESSAGE);
							break;
					}
					return;

				}

				UI.getCurrent().getNavigator().navigateTo(UserView.NAME);
			}
		}
	}

	private boolean isUsernameInUse(String username)
	{
		boolean inUse = false;
		UserDao daoUser = new UserDao();

		User usedBy = daoUser.getByName(username);

		// true if we have at least one element that isn't the currently edited user.
		if (this.isEdit)
			inUse = !usedBy.equals(this.editedUser);
		else
			inUse = usedBy == null;

		return inUse;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
