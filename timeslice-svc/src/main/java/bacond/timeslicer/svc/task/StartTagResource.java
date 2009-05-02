package bacond.timeslicer.svc.task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

import bacond.lib.restlet.FormattedStringTextPlainRenderer;
import bacond.lib.restlet.JsonObjectRenderer;
import bacond.lib.restlet.ToStringRepr;
import bacond.lib.util.ITransform;
import bacond.lib.util.MapMaker;
import bacond.lib.util.Transforms;
import bacond.timeslicer.app.task.StartTag;
import bacond.timeslicer.restletservice.MyApp;
import bacond.timeslicer.timeslice.TimesliceApp;

/**
 * Resource for a {@link StartTag}.
 *
 * <p>
 * <table>
 * <tr><td>GET</td>    <td>Return representation entity of identified resource.</td></tr>
 * <tr><td>PUT</td>    <td><i>Update</i> an existing, identified resource from a given representation entity.</td></tr>
 * <tr><td>POST</td>   <td><i>N/A</i></td></tr>
 * <tr><td>DELETE</td> <td><i>Delete</i> an existing, identified resource.</td></tr>
 * </table>
 * </p>
 *
 * @author dbacon
 *
 */
public class StartTagResource extends Resource
{
	private static final Logger log = Logger.getLogger(StartTagResource.class.getCanonicalName());

	private final Map<MediaType, ITransform<StartTag, Representation>> renderers = MapMaker.create(new LinkedHashMap<MediaType, ITransform<StartTag, Representation>>())
			.put(MediaType.APPLICATION_JSON, new JsonObjectRenderer<StartTag>())
			.put(MediaType.TEXT_PLAIN, new FormattedStringTextPlainRenderer<StartTag>(new ToString("[%1$s#%2$s#%3$s]", 9)))
			.put(MediaType.TEXT_HTML, Transforms.compose(
					new ToString(
						"<table>" +
						"<tr><td>when</td><td>:</td><td><b>%1$s</b></td></tr>" +
						"<tr><td>what</td><td>:</td><td><b>%2$s</b></td></tr>" +
						"</table>", 9),
					new ToStringRepr(MediaType.TEXT_HTML)))
			.getMap();

	public StartTagResource(Context context, Request request, Response response)
	{
		super(context, request, response);

		setModifiable(true); // set this false if caller is now owner ?

		for (MediaType mediaType: renderers.keySet())
		{
			getVariants().add(new Variant(mediaType));
		}
	}

	protected MyApp getMyApp()
	{
		return (MyApp) getApplication();
	}

	protected TimesliceApp getTimesliceApp()
	{
		return getMyApp().getTimesliceApp();
	}

	private StartTag lookupStartTag()
	{
		String key = (String) getRequest().getAttributes().get("when");

		if (null == key)
		{
			return null;
		}

		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

		StartTag startTag = getTimesliceApp().getStartTagStore().find(new Instant(fmt.parseMillis(key)));

		return startTag;
	}

	private StartTag removeStartTag(StartTag startTag)
	{
		StartTag removedTag = null;

		if (getTimesliceApp().getStartTagStore().contains(startTag.getWhen()))
		{
			removedTag = getTimesliceApp().getStartTagStore().remove(startTag.getWhen());
		}

		return removedTag;
	}

	private StartTag updateStartTag(Instant instant, StartTag updateTag)
	{
		if (getTimesliceApp().getStartTagStore().contains(instant))
		{
			/* StartTag oldTag = */getTimesliceApp().getStartTagStore().enterTag(new StartTag(updateTag.getWho(), instant, updateTag.getWhat(), updateTag.getUntil()));
		}
		else
		{
			throw new RuntimeException("Internal error, expected to find starttag, but wasnt found.");
		}

		return getTimesliceApp().getStartTagStore().find(updateTag.getWhen());
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		log.info("represent");

		StartTag startTag = lookupStartTag();

		if (null == startTag)
		{
			return null;
		}

		ITransform<StartTag, Representation> renderer = renderers.get(variant.getMediaType());

		if (null == renderer)
		{
			return null;
		}

		return renderer.apply(startTag);
	}

	@Override
	public boolean allowPost()
	{
		return false;
	}

	/**
	 * AKA HTTP 'PUT', an update of existing resource.
	 */
	@Override
	public void storeRepresentation(Representation entity) throws ResourceException
	{
		log.info("storeRepresentation");
		StartTag localTag = lookupStartTag();

		if (null == localTag)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
		else
		{
			List<StartTag> newTags = new StartTagHelper().parseEntity(entity, getResponse());
			for (StartTag newTag: newTags)
			{
				updateStartTag(localTag.getWhen(), newTag);
			}
		}
	}

	/**
	 * AKA HTTP 'DELETE'.
	 */
	@Override
	public void removeRepresentations() throws ResourceException
	{
		log.info("removeRepresentations");
		StartTag localTag = lookupStartTag();

		if (null == localTag)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}

		StartTag removedTag = removeStartTag(localTag);

		if (null == removedTag)
		{
			log.warning("removed tag was null.");
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else
		{
			getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
		}
	}

}
