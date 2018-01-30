package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pi4j.io.gpio.Pin;

import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.Lighting;

public class LightingDao
{

	@SuppressWarnings("unchecked")
	public List<Lighting> getAll()
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query query = em.createQuery("SELECT e FROM Lighting e");
		return (List<Lighting>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Lighting> getBySwitch(EndPoint lightSwitch)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query query = em.createQuery("SELECT e FROM Lighting e where e.lightSwitch = :lightSwitch");
		query.setParameter("lightSwitch", lightSwitch);

		return (List<Lighting>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<EndPoint> getByPin(Pin piPin)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query query = em
				.createQuery("SELECT e FROM EndPoint e where e.pinNo = :pinNo order by LOWER(e.endPointName)");
		query.setParameter("pinNo", piPin.getAddress());

		return (List<EndPoint>) query.getResultList();
	}

	public void deleteAll()
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		Query q2 = em.createQuery("DELETE FROM Lighting e");
		q2.executeUpdate();
	}

	public void deleteByEndPoint(EndPoint endPoint)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		Query query = em.createQuery("DELETE FROM Lighting e where e.lightSwitch = :lightSwitch");
		query.setParameter("lightSwitch", endPoint);
		query.executeUpdate();
	}

	public void persist(Lighting lighting)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.persist(lighting);
	}

	public void delete(Lighting lighting)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		// make certain we are deleting an attached entity.
		lighting = em.find(Lighting.class, lighting.getId());

		em.remove(lighting);

	}

	public void merge(Lighting Lighting)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.merge(Lighting);

	}

}
