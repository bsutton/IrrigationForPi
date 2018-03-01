package au.org.noojee.irrigation;

import com.vaadin.navigator.Navigator;

import au.org.noojee.irrigation.views.ForgottenPasswordView;
import au.org.noojee.irrigation.views.FirstRunView;
import au.org.noojee.irrigation.views.LoginView;
import au.org.noojee.irrigation.views.ResetPasswordView;

public class PublicNavigator extends Navigator
{
	private static final long serialVersionUID = 1L;

	PublicNavigator(ControllerUI controllerUI)
	{
		super(controllerUI, controllerUI);

		if (PiGationConfig.SELF.isConfigured())
		{
			LoginView loginView = new LoginView();
			this.setErrorView(loginView);
			this.addView("", loginView);
			this.addView(LoginView.NAME, loginView);
			this.addView(ForgottenPasswordView.NAME, ForgottenPasswordView.class);
			this.addView(ResetPasswordView.NAME, ResetPasswordView.class);
			this.addView(FirstRunView.NAME, ResetPasswordView.class);
		}
		else
		{
			// The system hasn't been configured so force the user to configure us.
			FirstRunView installView = new FirstRunView();
			this.setErrorView(installView);
			this.addView("", installView);
			this.addView(FirstRunView.NAME, installView);
	
			
		}
	}

}
