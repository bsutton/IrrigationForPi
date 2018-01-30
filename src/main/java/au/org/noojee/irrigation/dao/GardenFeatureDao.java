package au.org.noojee.irrigation.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.pi4j.io.gpio.Pin;

import au.org.noojee.irrigation.entities.EndPoint;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.entities.GardenFeature;

public class GardenFeatureDao
{

	public GardenFeature getById(long id)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		return em.find(GardenFeature.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<GardenBed> getAll()
	{
		EntityManager em = EntityManagerProvider.getEntityManager();

		Query query = em.createQuery("SELECT e FROM GardenFeature e");
		return (List<GardenBed>) query.getResultList();
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

		Query q2 = em.createQuery("DELETE FROM GardenFeature e");
		q2.executeUpdate();
	}

	public void persist(GardenFeature gardenFeature)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.persist(gardenFeature);
	}

	public void delete(GardenFeature gardenFeature)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		gardenFeature = em.find(GardenFeature.class, gardenFeature.getId());

		em.remove(gardenFeature);

	}

	public void merge(GardenFeature gardenFeature)
	{
		EntityManager em = EntityManagerProvider.getEntityManager();
		em.merge(gardenFeature);
	}



	
}
