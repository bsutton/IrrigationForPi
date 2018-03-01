package au.org.noojee.irrigation.views;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.ControllerUI;
import au.org.noojee.irrigation.dao.UserDao;
import au.org.noojee.irrigation.entities.User;

public class LoginView extends FormLayout implements SmartView
{
	private static final long serialVersionUID = 1L;
	static public final String NAME = "Login";
	private TextField usernameField;
	private PasswordField passwordField;

	@Override
	public String getName()
	{
		return NAME;
	}

	public LoginView()
	{
		usernameField = new TextField("Username");
		this.addComponent(usernameField);
		passwordField = new PasswordField("Password");
		this.addComponent(passwordField);

		Button btnLogin = new Button("Login");
		this.addComponent(btnLogin);
		btnLogin.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btnLogin.addClickListener(e -> login(e));
		btnLogin.setClickShortcut(KeyCode.ENTER);

		Button btnForgottenPassword = new Button("Forgotten Password");
		this.addComponent(btnForgottenPassword);
		btnForgottenPassword.addClickListener(e -> forgottenPassword(e));

	}

	private void forgottenPassword(ClickEvent e)
	{
		UI.getCurrent().getNavigator().navigateTo(ForgottenPasswordView.NAME);
	}

	private void login(ClickEvent e)
	{
		UserDao daoUser = new UserDao();

		String username = usernameField.getValue().trim();
		String password = passwordField.getValue().trim();

		User user = daoUser.authenticate(username, password);

		if (user != null)
		{
			UserDao.login(user);

			((ControllerUI) UI.getCurrent()).loadLayout();
			UI.getCurrent().getNavigator().navigateTo(OverviewView.NAME);
		}
		else
		{
			user = daoUser.getByName(username);
			if (user == null)
				Notification.show("Invalid username", "The given username is unknown.", Type.ERROR_MESSAGE);
			else
				Notification.show("Invalid password", "The given password does not match.", Type.ERROR_MESSAGE);
		}
	}

}
