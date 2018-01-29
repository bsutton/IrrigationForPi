package au.org.noojee.irrigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

import com.google.gson.Gson;

/**
 * A convenience method to store you Pi-gation configuration in a resource file (that you keep out of git). 
 * 
 * This file is normally created during the installation process and/or via the ConfigurationView.
 * 
 * Use:
 * PiGationConfig config = PiGationConfig.load(); 
 * 
 * The config file must be on your class path and must be called: config.json.
 * Normally you would place it in src/main/resources/config.json
 * 
 * The contents of the must be of the form: 
 * 
 * {
 * "password":"db password"
 * , "logPath":"path to store log file"
 * , "dbPath":"path to store database"
 * }
 * 
 * @author bsutton
 */

public class PiGationConfig
{

	private static final String CONFIG_JSON = "config.json";
	private String password;

	PiGationConfig(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return password;
	}

	public static PiGationConfig load() throws FileNotFoundException
	{
		ClassLoader classLoader = PiGationConfig.class.getClassLoader();

		// The file must be located in the resource directory.
		URL resource = classLoader.getResource(CONFIG_JSON);
		if (resource == null)
			throw new FileNotFoundException(CONFIG_JSON);

		File file = new File(resource.getFile());

		FileReader fr = new FileReader(file);

		PiGationConfig secret = new Gson().fromJson(fr, PiGationConfig.class);

		return secret;
	}

}
