package au.org.noojee.irrigation.jetty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

/**
 * <p>
 * A specialized Jetty {@link Server} to run Vaadin applications.
 * </p>
 * <p>
 * Server HTTP port, UI implementation, UIProvider, webapp directory, and application context path are configurable
 * through the parameters in the available constructors.
 * </p>
 * <p>
 * If used to test Vaadin Add-ons, please refer to the documentation at
 * <a href="https://github.com/alejandro-du/vaadin-jetty"> https://github.com/alejandro-du/vaadin-jetty</a>.
 * </p>
 *
 * @author alejandro@vaadin.com
 */
public class MyVaadinJettyServer extends Server
{

	private static final String DEFAULT_WEBAPP_DIRECTORY = "target/classes";
	private static final String DEFAULT_CONTEXT_PATH = "/";

	public MyVaadinJettyServer(int port) throws IOException
	{
		this(port, DEFAULT_WEBAPP_DIRECTORY, DEFAULT_CONTEXT_PATH);
	}

	public MyVaadinJettyServer(int port, String webappDirectory) throws IOException
	{
		this(port, webappDirectory, DEFAULT_CONTEXT_PATH);
	}

	public MyVaadinJettyServer(int port, String webappDirectory, String contextPath) throws IOException
	{
		super(port);

		WebAppContext context = new WebAppContext(webappDirectory, contextPath);
		context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/" + webappDirectory + "/.*");
		context.setConfigurationDiscovered(true);
		context.setConfigurations(new Configuration[]
		{
				new AnnotationConfiguration(),
				new WebInfConfiguration(),
				new WebXmlConfiguration(),
				new MetaInfConfiguration(),
				new FragmentConfiguration(),
				new EnvConfiguration(),
				new PlusConfiguration(),
				new JettyWebXmlConfiguration()
		});

		ResourceCollection resources = new ResourceCollection(new String[]
		{
				"src/main/webapp",
		});
		context.setBaseResource(resources);

		createIfDoesntExists(webappDirectory);

		setHandler(context);
	}

	private void createIfDoesntExists(String directory) throws IOException
	{
		Path path = Paths.get(directory);
		if (!Files.exists(path))
		{
			Files.createDirectory(path);
		}
	}

}
