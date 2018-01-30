package au.org.noojee.irrigation.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class ErrorWindow
{
	private static Logger logger = LogManager.getLogger();
	
	public static void showErrorWindow(Exception e)
	{
		logger.error(e, e);
		Notification.show("Oops, something bad happened", e.getMessage(), Type.ERROR_MESSAGE);
	}

}
