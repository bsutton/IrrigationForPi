package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.org.noojee.irrigation.entities.GardenBed;

public class GardenBedDao
{

	@SuppressWarnings("unchecked")
	@InjectEntity
	public List<GardenBed> getAll()
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		Query query = em.createQuery("SELECT e FROM GardenBed e");
		return (List<GardenBed>) query.getResultList();
	}

	public void persist(GardenBed gardenBed)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.persist(gardenBed);
			tran.commit();
		}

	}

	public void delete(GardenBed GardenBed)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			// make certain we are deleting an attached entity.
			GardenBed = em.find(GardenBed.class, GardenBed.getId());

			em.remove(GardenBed);
			tran.commit();
		}

	}

	public void merge(GardenBed GardenBed)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		try (Transaction tran = new Transaction(em))
		{
			em.merge(GardenBed);
			tran.commit();
		}
		
	}

}
