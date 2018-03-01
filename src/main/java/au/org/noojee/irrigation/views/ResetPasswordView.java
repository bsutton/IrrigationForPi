package au.org.noojee.irrigation.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import au.org.noojee.irrigation.dao.UserDao;
import au.org.noojee.irrigation.entities.User;

public class ResetPasswordView extends FormLayout implements View
{
	private static final long serialVersionUID = 1L;
	static public final String NAME = "ResetPasswordView";
	TextField passwordField = new TextField("Password");
	TextField confirmPasswordField = new TextField("Confirm Password");
	private String securityToken;

	public ResetPasswordView()
	{
		Label heading = new Label("<h1>Reset Password</h1>");
		this.addComponent(heading);
		heading.setContentMode(ContentMode.HTML);
		
		this.addComponent(passwordField);
		this.addComponent(confirmPasswordField);

		HorizontalLayout buttons = new HorizontalLayout();
		this.addComponent(buttons);

		Button btnCancel = new Button("Cancel");
		buttons.addComponent(btnCancel);
		Button btnSave = new Button("Save");
		buttons.addComponent(btnSave);

		btnSave.addClickListener(l -> onSave());
		btnCancel.addClickListener(l -> onCancel());
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		securityToken = event.getParameters();

		UserDao daoUser = new UserDao();
		User user = daoUser.getBySecurityToken(securityToken);

		if (user == null || !user.isSecurityTokenLive())
		{
			Notification.show("Expired Token", "Your password reset token has expired. Request a new password reset",
					Type.ERROR_MESSAGE);
			UI.getCurrent().getNavigator().navigateTo(ForgottenPasswordView.NAME);
		}
	}

	private void onCancel()
	{
		UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
	}

	void onSave()
	{
		String password = passwordField.getValue().trim();
		String confirmPassword = confirmPasswordField.getValue().trim();
		if (password.equals(confirmPassword))
		{
			if (password.length() < 12)
				Notification.show("Password too short", "The password must be at least 12 characters long.",
						Type.ERROR_MESSAGE);
			else
			{
				UserDao daoUser = new UserDao();
				User user = daoUser.getBySecurityToken(securityToken);

				if (user == null || !user.isSecurityTokenLive())
				{
					Notification.show("Expired Token",
							"Your password reset token has expired. Request a new password reset", Type.ERROR_MESSAGE);
					UI.getCurrent().getNavigator().navigateTo(ForgottenPasswordView.NAME);
				}
				else
				{
					
					user.setPassword(password);
					user.setSecurityToken(null);
					user.setTokenExpiryDate(null);
					
					Notification.show("Password reset", "Your password has been reset.",
							Type.WARNING_MESSAGE);
		
					UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
				}
			}
		}
		else
			Notification.show("Invalid Password Pair", "The password fields don't match", Type.ERROR_MESSAGE);

	}

}
