package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.entities.History;

public class HistoryDao
{

	@SuppressWarnings("unchecked")
	public List<History> getAll()
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query query = em.createQuery("SELECT e FROM History e order by e.eventStart desc");
		return (List<History>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<History> getByGardenBed(GardenBed gardenBed)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query query = em.createQuery("SELECT e FROM History where e.gardenBed = :gardenBed");
		query.setParameter("gardenBed", gardenBed);

		return (List<History>) query.getResultList();
	}

//	@SuppressWarnings("unchecked")
//	public void deleteByGardenBed(GardenBed gardenBed)
//	{
//		EntityManager em = MyEntityManagerUtil.getEntityManager();
//
//		try (Transaction tran = new Transaction(em))
//		{
//			Query query = em.createQuery("SELECT h FROM History h where h.gardenBed = :gardenBed");
//			query.setParameter("gardenBed", gardenBed);
//
//
//			List<History> historyList = (List<History>) query.getResultList();
//
//			historyList.stream().forEach(h -> em.remove(h));
//
//			tran.commit();
//		}
//	}

//	public void persist(History History)
//	{
//		EntityManager em = MyEntityManagerUtil.getEntityManager();
//
//		try (Transaction tran = new Transaction(em))
//		{
//			em.persist(History);
//			tran.commit();
//		}
//
//	}

//	public void delete(History History)
//	{
//		EntityManager em = MyEntityManagerUtil.getEntityManager();
//
//		try (Transaction tran = new Transaction(em))
//		{
//			// make certain we are deleting an attached entity.
//			History = em.find(History.class, History.getId());
//
//			em.remove(History);
//			tran.commit();
//		}
//
//	}
//
	public void merge(History History)
	{
		EntityManagerProvider.getEntityManager().merge(History);
	}

}
