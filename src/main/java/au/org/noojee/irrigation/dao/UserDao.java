package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
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

	
	@SuppressWarnings("unchecked")
	public List<User> getByName(String username)
	{

		EntityManager em = EntityManagerProvider.getEntityManager();
		Query query = em.createQuery("SELECT e FROM User e where e.username = :username");
		query.setParameter("username", username);

		return (List<User>) query.getResultList();
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
