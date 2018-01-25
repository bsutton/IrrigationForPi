package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pi4j.io.gpio.Pin;

import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;

public class GardenBedDao
{

	@SuppressWarnings("unchecked")
	public List<GardenBed> getAll()
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		Query query = em.createQuery("SELECT e FROM GardenBed e");
		return (List<GardenBed>) query.getResultList();
	}
	
	/**
	 * Returns all garden beds which are controlled by the given master valve.
	 * @param masterValve
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GardenBed> getControlledBy(EndPoint masterValve)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		Query query = em.createQuery("SELECT e FROM GardenBed e where e.masterValve = :masterValve");
		query.setParameter("masterValve", masterValve);
		
		return (List<GardenBed>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GardenBed> getByValve(EndPoint valve)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		Query query = em.createQuery("SELECT e FROM GardenBed e where e.valve = :valve");
		query.setParameter("valve", valve);
		
		return (List<GardenBed>) query.getResultList();
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


	public void persist(GardenBed gardenBed)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.persist(gardenBed);
			tran.commit();
		}

	}

	public void delete(GardenBed gardenBed)
	{
//		// We have to delete the history first.
//		HistoryDao daoHistory = new HistoryDao();
//		daoHistory.deleteByGardenBed(gardenBed);

		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			// make certain we are deleting an attached entity.
			gardenBed = em.find(GardenBed.class, gardenBed.getId());

			em.remove(gardenBed);
			tran.commit();
		}

	}

	public void merge(GardenBed GardenBed)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.merge(GardenBed);
			tran.commit();
		}
		
	}



}
