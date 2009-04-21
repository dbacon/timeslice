package bacond.timeslicer.app.auth;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.restlet.util.Resolver;

import bacond.lib.util.Narrow;

public class AclFile extends Resolver<char[]>
{
	// why is Resolver a class and not an interface?

	private final String fileName;

	public AclFile(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileName()
	{
		return fileName;
	}

	@Override
	public char[] resolve(String name)
	{
		Map<String, String> map = readFileIntoMap();

		if (map.containsKey(name))
		{
			return map.get(name).toCharArray();
		}
		else
		{
			return null;
		}
	}

	private Map<String, String> readFileIntoMap()
	{

		try
		{
			Map<String, String> map = new LinkedHashMap<String, String>();

			for (String line: Narrow.<String>fromList(FileUtils.readLines(new File(getFileName()))))
			{
				String[] fields = line.split(":");

				if (2 == fields.length)
				{
					String user = fields[0];
					String pass = fields[1];

					map.put(user, pass);
				}
			}

			return map;

		}
		catch (IOException e)
		{
			throw new RuntimeException("Exception reading password file: " + e.getMessage(), e);
		}
	}
}
