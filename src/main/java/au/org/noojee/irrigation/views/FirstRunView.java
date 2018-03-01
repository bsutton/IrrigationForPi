package au.org.noojee.irrigation.views;

import java.io.IOException;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.PiGationConfig;

public class FirstRunView extends FormLayout implements View
{
	private static final long serialVersionUID = 1L;

	public static final String NAME = "FirstRunView";

	private TextField usernameField;
	private TextField passwordField;
	private TextField smtpServerField;
	private TextField smtpPortField;

	public FirstRunView()
	{
		this.setSizeFull();
		this.setMargin(true);

		Label heading = new Label("<h1>Your system needs to be configured</h1></p>"
				+ "<h2>Please enter the required details below.</h2>");

		heading.setContentMode(ContentMode.HTML);
		this.addComponent(heading);

		usernameField = new TextField("Database Username: ");
		this.addComponent(usernameField);
		usernameField.setWidth("100%");
		usernameField.setValue("tomcat");

		passwordField = new TextField("Database Password: ");
		this.addComponent(passwordField);
		passwordField.setWidth("100%");

		smtpServerField = new TextField("SMTP Server : ");
		this.addComponent(smtpServerField);
		smtpServerField.setWidth("100%");
		smtpServerField.setValue("mail.noojeeit.com.au");

		smtpPortField = new TextField("SMTP Server Port(25): ");
		this.addComponent(smtpPortField);
		smtpPortField.setWidth("100%");
		smtpPortField.setValue("25");

	
		Button btnSave = new Button("Save");
		this.addComponent(btnSave);
		btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);

		btnSave.addClickListener(l -> save());

	}

	private void save()
	{
		PiGationConfig.SELF.setUsername(getValue(usernameField));
		PiGationConfig.SELF.setPassword(getValue(passwordField));
		PiGationConfig.SELF.setSMTPServer(getValue(smtpServerField));
		PiGationConfig.SELF.setSMTPPort(Integer.valueOf(getValue(smtpPortField)));
		

		try
		{
			PiGationConfig.save();
			
			Notification.show("Configuration Saved!", "You must now restart the Tomcat service.", Type.ERROR_MESSAGE);
		}
		catch (IOException e)
		{
			Notification.show("Error writing configuration", e.getMessage(), Type.ERROR_MESSAGE);
		}
	}

	private String getValue(TextField field)
	{
		return field.getValue().trim();
	}
}
