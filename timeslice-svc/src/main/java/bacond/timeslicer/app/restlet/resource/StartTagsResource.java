package bacond.timeslicer.app.restlet.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import bacond.lib.restlet.FormattedStringTextPlainRenderer;
import bacond.lib.util.ITransform;
import bacond.lib.util.MapMaker;
import bacond.timeslicer.app.dto.StartTag;
import bacond.timeslicer.app.processing.Split;

public class StartTagsResource extends Resource
{
	private static final Logger log = Logger.getLogger(StartTagsResource.class.getCanonicalName());
	
	private final Map<MediaType, ITransform<Collection<StartTag>, Representation>> renderers = MapMaker.create(new LinkedHashMap<MediaType, ITransform<Collection<StartTag>, Representation>>())
		.put(MediaType.TEXT_PLAIN, FormattedStringTextPlainRenderer.create(new TextPlainStartTagsFormatter()))
		.put(MediaType.TEXT_HTML,
				ToStringRepr.create(MediaType.TEXT_HTML,
						HtmlPagifier.pagify(BulletedListTextHtmlRenderer.create(new ToString("[<small><a href=\"/items/%1$s\">%1$s</a></small>] %2$s")))))
		.put(MediaType.APPLICATION_JSON, new JsonArrayRenderer<StartTag>())		
		.getMap();

	public StartTagsResource(Context context, Request request, Response response)
	
	{
		super(context, request, response);
		
		for (MediaType mediaType: renderers.keySet())
		{
			getVariants().add(new Variant(mediaType));
		}
	}

	protected MyApp getMyApp()
	{
		return (MyApp) getApplication();
	}
	
	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		MediaType mediaType = variant.getMediaType();
		
		String requestedMediaType = (String) getRequest().getAttributes().get("mediatype");
		if (null != requestedMediaType)
		{
			mediaType = MediaType.valueOf(requestedMediaType);
		}
			
		log.info("rendering items for " + variant.getMediaType());
		
//		String enrichType = (String) getRequest().getAttributes().get("enrich");
//		if ("link".equals(enrichType))
//		{
//		}
		
		Comparator<StartTag> cmp = new CompareByTime();
		
		String sortColumn = (String) getRequest().getAttributes().get("sortdir");
		if (null != sortColumn && "desc".equals(sortColumn))
		{
			cmp = Collections.reverseOrder(cmp);
		}
		
		List<StartTag> tags = new LinkedList<StartTag>(getMyApp().getStartTags().values());

		if (true)
		{
			tags = new Split().split(tags, new Instant());
		}
		
		Collections.sort(tags, cmp);
		
		String max = (String) getRequest().getAttributes().get("max");
		if (null != max)
		{
			tags = tags.subList(0, Math.min(tags.size(), Integer.valueOf(max)));
		}
		
		return renderers.get(mediaType).apply(tags);
	}

	@Override
	public boolean allowPost()
	{
		return true;
	}

	@Override
	public void acceptRepresentation(Representation entity) throws ResourceException
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
			getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Only form and text-plain is acceptable.");
			return;
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
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
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
		
		StartTag startTag = new StartTag(who, whenInstant, what, untilInstant);
		
		if (getMyApp().getStartTags().containsKey(startTag.getWhen()))
		{
			getResponse().setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
			return;
		}
		
		// TODO: better integration/validation support upon adding startTag to list.
		//  for example collisions, missing untils if in past, etc.
		getMyApp().getStartTags().put(startTag.getWhen(), startTag);
		
		getResponse().setStatus(Status.SUCCESS_CREATED, "created.");
	}

	@Override
	public void storeRepresentation(Representation entity)
			throws ResourceException
	{
		log.info("storeRepresentation()");
		super.storeRepresentation(entity);
	}
}
