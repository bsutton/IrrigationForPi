package au.org.noojee.irrigation.entities;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Injects an entity manager into the current thread.
 * Just add @InjectEntity before any method that needs an entity manager.
 * Any method called from that method will also have access to the entity manager.
 * 
 * Call EntityAspect.getEntityManager to get the thread entity manager.
 * 
 * @author bsutton
 *
 */
@Aspect
public class EntityAspect
{
	private static final Logger logger = LogManager.getLogger();

	private static ThreadLocal<EntityManager> threadLocalEM = ThreadLocal.withInitial(() -> EntityManagerUtil.createEntityManager());
	private static ThreadLocal<EntityTransaction> threadLocalTransaction = ThreadLocal.withInitial(() -> threadLocalEM.get().getTransaction());

	// Defines a pointcut where the @InjectEntity exists
	// And combines that with a catch all pointcut with the scope of execution
	@Around("@annotation(InjectEntity) && execution(* *(..))")

	// ProceedingJointPoint = the reference of the call to the method.
	// Difference between ProceedingJointPoint and JointPoint is that a JointPoint can't be continued(proceeded)
	// A ProceedingJointPoint can be continued(proceeded) and is needed for a Around advice
	public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable
	{
		// Default Object that we can use to return to the consumer
		Object returnObject = null;
		try
		{

			//EntityManager entityManager = threadLocalEM.get();
			EntityTransaction tran = threadLocalTransaction.get();
			
			boolean alreadyActive = tran.isActive();

			try 
			{

				if (!alreadyActive)
					tran.begin();
				

				logger.debug("Entity Transaction Begin");
				// We choose to continue the call to the method in question
				returnObject = joinPoint.proceed();
				// If no exception is thrown we should land here and we can modify the returnObject, if we want to.

				
				if (!alreadyActive)
					tran.commit();
				logger.debug("Entity Transaction Committed");
			}
			catch (Throwable e)
			{
				logger.debug("Entity Transaction Rollback");
				
				
				if (!alreadyActive) // leave the top level to do the rollback.
					tran.rollback();
			}

			
		}
		catch (Throwable throwable)
		{
			// Here we can catch and modify any exceptions that are called
			// We could potentially not throw the exception to the caller and instead return "null" or a default object.
			throw throwable;
		}
		finally
		{
			// If we want to be sure that some of our code is executed even if we get an exception
			System.out.println("YourAspect's aroundAdvice's body is now executed After yourMethodAround is called.");
		}
		return returnObject;
	}
	
	static EntityManager getThreadEntityManager()
	{
		return threadLocalEM.get();
	}
	

}