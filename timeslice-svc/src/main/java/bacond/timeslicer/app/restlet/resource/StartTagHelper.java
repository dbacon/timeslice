package bacond.timeslicer.app.restlet.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.Instant;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

import bacond.lib.util.Transforms;
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
	public List<StartTag> parseEntity(Representation entity, Response response)
	{
		List<StartTag> result = new ArrayList<StartTag>();

		if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true))
		{
			log.info("accepting multi-part form-data post");
			result.add(new BulkText(entity).handleBulk(response));
		}
		else
		{
			if (MediaType.TEXT_PLAIN.equals(entity.getMediaType()))
			{
				log.info("accepting text-plain post");
				
				TextRepr textRepr = new TextRepr(entity);
				
				result.add(deal(textRepr.get("who"), textRepr.get("when"), textRepr.get("until"), textRepr.get("what"), response));
			}
			else if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType()))
			{
				log.info("accepting form post");
				
				Form form = new Form(entity);

				result.add(deal(form.getFirstValue("who"), form.getFirstValue("when"), form.getFirstValue("until"), form.getFirstValue("what"), response));
			}
			else
			{
				log.info("not accepting post; only form and text-plain is acceptable.");
				response.setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Only form and text-plain is acceptable.");
			}
		}
		
		return result;
	}
	
	public static class BulkText
	{
		private final Representation entity;
		
		public BulkText(Representation entity)
		{
			this.entity = entity;
			
			// TODO: LEFTOFF: parse uploaded data.
		}

		public StartTag handleBulk(Response response)
		{
			return new StartTag("bacond", new Instant(), "FROM UPLOAD: " + entity.getSize() + " bytes", null);
		}
	}
	

	private StartTag deal(String who, String when, String until, String what, Response response)
	{
		Instant whenInstant = Transforms.mapNullTo(StartTagsResource.parseInstantIfAvailable(when), new Instant());
		Instant untilInstant = Transforms.mapNullTo(StartTagsResource.parseInstantIfAvailable(until), new Instant());
		who = Transforms.mapNullTo(who, "anonymous");
		// TODO authenticate 'who' ?
		
		StartTag newStartTag = new StartTag(who, whenInstant, what, untilInstant);
		
		if (null == newStartTag.getWhat())
		{
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		return newStartTag;
	}
}
