package au.org.noojee.irrigation.dao;


public interface EntityWorker<T>
{

	T exec() throws Exception;

}
