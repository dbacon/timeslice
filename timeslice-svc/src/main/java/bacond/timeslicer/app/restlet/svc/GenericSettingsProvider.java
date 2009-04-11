package bacond.timeslicer.app.restlet.svc;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

public class GenericSettingsProvider
{
	public Properties readSettings()
	{
		String homeDir = System.getProperty("user.home", ".");

		String rcFilename = FilenameUtils.concat(homeDir, ".timeslicerc");
		File rcfile = new File(rcFilename);

		System.out.println("Reading settings file: " + rcfile);

		Properties settings = new Properties();

		try
		{
			settings.load(new FileInputStream(rcfile));
		}
		catch (Exception e)
		{
			System.err.println("Exception reading settings: " + e.getMessage());
		}

		return settings;
	}

	public void writeSettings(Properties settings)
	{
		throw new RuntimeException("Not implemented.");
	}
}
