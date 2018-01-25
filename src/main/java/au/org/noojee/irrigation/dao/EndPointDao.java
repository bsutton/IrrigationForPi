package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pi4j.io.gpio.Pin;

import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.types.EndPointType;

public class EndPointDao
{

	@SuppressWarnings("unchecked")
	@InjectEntity
	public List<EndPoint> getAll()
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

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
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		Query query = em
				.createQuery("SELECT e FROM EndPoint e where e.endPointType = :type order by LOWER(e.endPointName)");
		query.setParameter("type", type);

		return (List<EndPoint>) query.getResultList();
	}


	@SuppressWarnings("unchecked")
	public List<EndPoint> getByPin(Pin piPin)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		Query query = em
				.createQuery("SELECT e FROM EndPoint e where e.pinNo = :pinNo order by LOWER(e.endPointName)");
		query.setParameter("pinNo", piPin.getAddress());

		return (List<EndPoint>) query.getResultList();
	}

	public void deleteAll()
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			Query q2 = em.createQuery("DELETE FROM EndPoint e");
			q2.executeUpdate();
			tran.commit();
		}
	}

	public void persist(EndPoint endPoint)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.persist(endPoint);
			tran.commit();
		}

	}

	public void delete(EndPoint endPoint)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			// make certain we are deleting an attached entity.
			endPoint = em.find(EndPoint.class, endPoint.getId());

			em.remove(endPoint);
			tran.commit();
		}

	}

	public void merge(EndPoint endPoint)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.merge(endPoint);
			tran.commit();
		}

	}

}
