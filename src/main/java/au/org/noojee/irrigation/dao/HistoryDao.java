package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.org.noojee.irrigation.entities.History;

public class HistoryDao
{

	@SuppressWarnings("unchecked")
	@InjectEntity
	public List<History> getAll()
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		Query query = em.createQuery("SELECT e FROM History e order by e.wateringEvent desc");
		return (List<History>) query.getResultList();
	}

	public void persist(History History)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.persist(History);
			tran.commit();
		}

	}

	public void delete(History History)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			// make certain we are deleting an attached entity.
			History = em.find(History.class, History.getId());

			em.remove(History);
			tran.commit();
		}

	}

	public void merge(History History)
	{
		EntityManager em = MyEntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.merge(History);
			tran.commit();
		}
		
	}

}
