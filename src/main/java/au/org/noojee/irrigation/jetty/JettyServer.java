package au.org.noojee.irrigation.jetty;

public class JettyServer
{

	public static void main(String[] args) throws Exception
	{

		MyVaadinJettyServer server = new MyVaadinJettyServer(8080);
		server.start();
		server.join();
	}

}
