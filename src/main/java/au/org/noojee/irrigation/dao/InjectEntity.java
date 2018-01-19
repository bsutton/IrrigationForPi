package au.org.noojee.irrigation.dao;


/**
 * injects an EntityManager into the thread (if it doesn't already exists) and 
 * starts a transaction.
 * 
 * When your method returns the transaction will be committed.
 * If a transaction already exists you will be joined to the existing transaction.
 * 
 * @author bsutton
 *
 */
public @interface InjectEntity
{

}
