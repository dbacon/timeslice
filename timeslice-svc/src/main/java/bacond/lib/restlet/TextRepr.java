package bacond.lib.restlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.representation.Representation;

/**
 * Supports format:
 *  attr1=val1
 *  attr2=val2
 *
 * @author dbacon
 *
 */
public class TextRepr
{
	private static final Logger log = Logger.getLogger(TextRepr.class.getCanonicalName());

	private final Map<String, String> map = new LinkedHashMap<String, String>();

	public TextRepr(Representation entity)
	{
		extractFromTextEntity(entity);
	}

	protected void extractFromTextEntity(Representation entity)
	{

		String reprText = null;

		try
		{
			reprText = entity.getText();
		}
		catch (IOException e)
		{
			reprText = "";
			log.info("Couldn't read representation: " + e.getMessage());
		}

		log.info("reprText: '" + reprText + "'");

		String[] pairs = reprText.split("\n", -1);

		for (String pair: pairs)
		{
			int barrier = pair.indexOf('=');

			if (barrier >= 0)
			{
				String name = pair.substring(0, barrier);
				String value = pair.substring(barrier + 1);
				getMap().put(name, value);
			}
		}
	}

	public Map<String, String> getMap()
	{
		return map;
	}

	public String get(String key)
	{
		return getMap().get(key);
	}
}
