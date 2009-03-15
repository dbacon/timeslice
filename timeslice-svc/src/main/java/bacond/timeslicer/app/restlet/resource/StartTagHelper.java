package bacond.timeslicer.app.restlet.resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import bacond.timeslicer.app.dto.StartTag;

public class StartTagHelper
{
	private static final Logger log = Logger.getLogger(StartTagsResource.class.getCanonicalName());

	/**
	 * Converts a given {@link Representation} into a {@link StartTag}.
	 * 
	 * @param entity
	 * @return <code>null</code> if <code>entity</code> could not be parsed into a {@link StartTag}.
	 */
	public StartTag parseEntity(Representation entity, Response response)
	{
		String who = null;
		String when = null;
		String until = null;
		String what = null;
		
		if (entity.getMediaType().equals(MediaType.TEXT_PLAIN))
		{
			log.info("accepting text-plain post");
			
			// Supports format:
			// attr1=val1
			// attr2=val2
			//  ...
			
			Map<String, String> attrs = new LinkedHashMap<String, String>();

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
					attrs.put(name, value);
				}
			}
			
			who = attrs.get("who");
			when = attrs.get("when");
			until = attrs.get("until");
			what = attrs.get("what");
		}
		else if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM))
		{
			log.info("accepting form post");

			Form form = new Form(entity);

			who = form.getFirstValue("who");
			when = form.getFirstValue("when");
			until = form.getFirstValue("until");
			what = form.getFirstValue("what");
		}
		else
		{
			log.info("not accepting post; only form and text-plain is acceptable.");
			response.setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Only form and text-plain is acceptable.");
			return null;
		}
		
		if (null != who)
		{
			// TODO: if who is provided, perhaps try to authenticate it and use it ?
		}
		
		if (null == who)
		{
			// TODO: get who from HTTP session / authenticated.
			who = "bacond";
		}
		
		if (null == what || null == who)
		{
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		Instant whenInstant = null;
		if (null != when)
		{
			whenInstant = ISODateTimeFormat.dateTime().parseDateTime(when).toInstant();
		}
		else
		{
			whenInstant = new Instant();
		}
		
		Instant untilInstant = null;
		if (null != until)
		{
			untilInstant = ISODateTimeFormat.dateTime().parseDateTime(until).toInstant();
		}
		
		return new StartTag(who, whenInstant, what, untilInstant);
	}
	
}
