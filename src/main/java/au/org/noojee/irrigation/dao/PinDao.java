package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.org.noojee.irrigation.entities.EntityManagerUtil;
import au.org.noojee.irrigation.entities.InjectEntity;
import au.org.noojee.irrigation.entities.Pin;
import au.org.noojee.irrigation.entities.Transaction;

public class PinDao
{

	@SuppressWarnings("unchecked")
	@InjectEntity
	public List<Pin> getAll()
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		Query query = em.createQuery("SELECT e FROM Pin e");
		return (List<Pin>) query.getResultList();
	}

	public void persist(Pin pin)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.persist(pin);
			tran.commit();
		}

	}

	public void delete(Pin pin)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			// make certain we are deleting an attached entity.
			pin = em.find(Pin.class, pin.getId());

			em.remove(pin);
			tran.commit();
		}

	}

	public void merge(Pin pin)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.merge(pin);
			tran.commit();
		}
		
	}

}
