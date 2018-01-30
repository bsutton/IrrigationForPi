package au.org.noojee.irrigation.servlets;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;

final class PgSessionListener implements SessionInitListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException
	{
		event.getSession().addRequestHandler(new PgRequestHandler());

		event.getSession().addBootstrapListener(new PgBootstrapListener());
	}
}