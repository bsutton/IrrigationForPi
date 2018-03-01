package au.org.noojee.irrigation.views;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import au.org.noojee.irrigation.PiGationConfig;
import au.org.noojee.irrigation.dao.UserDao;
import au.org.noojee.irrigation.entities.User;

@SuppressWarnings("serial")
public class ForgottenPasswordView extends FormLayout implements View
{
	static private final Logger logger = LogManager.getLogger();
	static public String NAME = "ForgottenPasswordView";

	private static final String NEWLINE = "\r\n";

	SecureRandom random = new SecureRandom();

	private TextField emailAddress;

	public ForgottenPasswordView()
	{
		emailAddress = new TextField("Email Address");
		this.addComponent(emailAddress);
		emailAddress.setPlaceholder("Enter your email address");
		emailAddress.setWidth(100, Unit.MM);

		Button btnSend = new Button("Send");
		this.addComponent(btnSend);
		btnSend.addClickListener(l -> send());

	}

	private void send()
	{
		
		String body = "Click the following link to reset your password." + NEWLINE;
		String token = generateToken();
		
		
		VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
		HttpServletRequest httpServletRequest = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest();
		String requestUrl = httpServletRequest.getRequestURL().toString();
		
		String link = requestUrl + "/ResetPasswordView/" + token;

		body += link + NEWLINE;
		
		// Finder user by email
		UserDao daoUser = new UserDao();
		User user = daoUser.getByEmailAddress(emailAddress.getValue().trim());
		
		if (user == null)
		{
			Notification.show("Error", "The entered email address is unknown.", Type.ERROR_MESSAGE);
		}
		else
		{
			user.setSecurityToken(token);
			user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(10));
			sendEmail(user, "Email reset", body);
			
			Notification.show("Reset sent", "A reset link has been sent to the entered email address.", Type.WARNING_MESSAGE);
			UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
		}
	}

	private String generateToken()
	{
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String token = encoder.encodeToString(bytes);
		return token;
	}

	private void sendEmail(User user, String subject, String body)
	{
		SimpleEmail email = new SimpleEmail();

		email.setHostName(PiGationConfig.SELF.getSmtpServer());
		email.setSmtpPort(PiGationConfig.SELF.getSmtpPort());

		try
		{
			email.setFrom("orionmonitor@noojee.com.au");
			email.setSubject(subject);
			email.setMsg(body);
			email.addTo(user.getEmailAddress());

			email.send();
		}
		catch (EmailException e)
		{
			logger.error(e, e);
		}
	}

}
