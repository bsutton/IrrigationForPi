package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.Pin;

import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.types.EndPointType;

public class EndPointDao
{
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger();

	@SuppressWarnings("unchecked")
	@InjectEntity
	public List<EndPoint> getAll()
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		Query query = em.createQuery("SELECT e FROM EndPoint e order by LOWER(e.endPointName)");
		return (List<EndPoint>) query.getResultList();
	}

	public List<EndPoint> getAllValves()
	{
		return getAllByType(EndPointType.Valve);
	}

	public List<EndPoint> getMasterValves()
	{
		return getAllByType(EndPointType.MasterValve);
	}

	@SuppressWarnings("unchecked")
	public List<EndPoint> getAllByType(EndPointType type)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		Query query = em
				.createQuery("SELECT e FROM EndPoint e where e.endPointType = :type order by LOWER(e.endPointName)");
		query.setParameter("type", type);

		return (List<EndPoint>) query.getResultList();
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
		Query q2 = em.createQuery("DELETE FROM EndPoint e");
		q2.executeUpdate();
	}

	public void persist(EndPoint endPoint)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.persist(endPoint);
	}

	public void delete(EndPoint endPoint)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		// make certain we are deleting an attached entity.
		endPoint = em.find(EndPoint.class, endPoint.getId());

		em.remove(endPoint);

	}

	public void merge(EndPoint endPoint)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.merge(endPoint);
	}

}
