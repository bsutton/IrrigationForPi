package au.org.noojee.irrigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A convenience method to store you Pi-gation configuration in a resource file (that you keep out of git). This file is
 * normally created during the installation process and/or via the ConfigurationView. Use: PiGationConfig config =
 * PiGationConfig.load(); The config file must be on your class path and must be called: config.json. Normally you would
 * place it in src/main/resources/config.json The contents of the must be of the form: { "password":"db password" ,
 * "logPath":"path to store log file" , "dbPath":"path to store database" }
 * 
 * @author bsutton
 */

public enum PiGationConfig
{
	SELF;
	
	private static final String CONFIG_JSON = "config.json";
	private String password;
	private int smtpPort;
	private String smtpServer;
	private String username;
	private boolean configured = false;


	public String getPassword()
	{
		return password;
	}
	
	public boolean isConfigured()
	{
		return configured;
	}


	public static void load() throws FileNotFoundException
	{
		ClassLoader classLoader = PiGationConfig.class.getClassLoader();

		// The file must be located in the resource directory.
		URL resource = classLoader.getResource(CONFIG_JSON);
		if (resource == null)
			throw new FileNotFoundException(CONFIG_JSON);

		File file = new File(resource.getFile());

		FileReader fr = new FileReader(file);

		PiGationConfig config= new Gson().fromJson(fr, PiGationConfig.class);
		
		SELF.username = config.username;
		SELF.password = config.password;
		SELF.smtpPort = config.smtpPort;
		SELF.smtpServer = config.smtpServer;
		SELF.configured = true;

	}

	public static void save() throws IOException
	{

		FileWriter orionWriter = new FileWriter(CONFIG_JSON);
		Gson orionGson = new GsonBuilder().create();
		orionGson.toJson(SELF, orionWriter);
		orionWriter.close();
		
		SELF.configured = true;

	}

	public void setUsername(String username)
	{
		this.username = username;
		
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setSMTPServer(String smtpServer)
	{
		this.smtpServer = smtpServer;
	}

	public void setSMTPPort(int smtpPort) 
	{
		this.smtpPort = smtpPort;
		
	}

	public int getSmtpPort()
	{
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	public String getSmtpServer()
	{
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer)
	{
		this.smtpServer = smtpServer;
	}

	public String getUsername()
	{
		return username;
	}


	

}
