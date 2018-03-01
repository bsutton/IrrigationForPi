package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.vaadin.server.VaadinSession;

import au.org.noojee.irrigation.entities.User;

public class UserDao
{

	@SuppressWarnings("unchecked")
	public List<User> getAll()
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query query = em.createQuery("SELECT e FROM User e");
		return (List<User>) query.getResultList();
	}

	
	public User getByName(String username)
	{

		EntityManager em = EntityManagerProvider.getEntityManager();
		Query query = em.createQuery("SELECT e FROM User e where e.username = :username");
		query.setParameter("username", username);

		// usernames are unique so there will only ever be one.
		User user = null;
		try
		{
			user = (User) query.getSingleResult();
		}
		catch (NoResultException e)
		{

		}

		return user;
	}
	
	public User getByEmailAddress(String emailaddress)
	{

		EntityManager em = EntityManagerProvider.getEntityManager();
		Query query = em.createQuery("SELECT e FROM User e where e.emailAddress = :emailaddress");
		query.setParameter("emailaddress", emailaddress);

		User user = null;
		try
		{
			user = (User) query.getSingleResult();
		}
		catch (NoResultException e)
		{

		}

		return user;

	}


	public User getBySecurityToken(String securityToken)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		Query query = em.createQuery("SELECT e FROM User e where e.securityToken = :securityToken");
		query.setParameter("securityToken", securityToken);

		User user = null;
		try
		{
			user = (User) query.getSingleResult();
		}
		catch (NoResultException e)
		{

		}

		return user;
	}

	public User authenticate(String username, String password)
	{

		User user = getByName(username);

		if (user != null && !Password.validate(password, user.getPassword()))
			user = null;

		return user;

	}

	public static void login(User user)
	{
		VaadinSession.getCurrent().setAttribute(User.class, user);
	}

	public static boolean isLoggedIn()
	{
		return VaadinSession.getCurrent().getAttribute(User.class) != null;
	}

	public static void logout()
	{
		VaadinSession.getCurrent().setAttribute(User.class, null);
	}

	public void deleteAll()
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query q2 = em.createQuery("DELETE FROM User e");
		q2.executeUpdate();
	}

	public void persist(User user)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.persist(user);
	}

	public void delete(User user)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		user = em.find(User.class, user.getId());

		em.remove(user);

	}

	public void merge(User user)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.merge(user);
	}

}
