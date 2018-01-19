package au.org.noojee.irrigation.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Transaction implements AutoCloseable
{
	Logger logger = LogManager.getLogger();
	private EntityTransaction transaction;

	public Transaction(EntityManager em)
	{
		transaction = em.getTransaction();
		transaction.begin();
	}

	public void commit()
	{
		transaction.commit();
	}
	
	@Override
	public void close()
	{
		if (transaction.isActive())
		{
			logger.error("Rolling back uncommitted transaction.");
			transaction.rollback();
		}
		
	}

}
