package bacond.timeslicer.app.restlet.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.Instant;
import org.restlet.Context;
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

/**
 * Resource for a collection of {@link StartTag}s.
 * 
 * <p>
 * <table>
 * <tr><td>GET</td>    <td>List them (table of contents, enumerate, &amp; c.).</td></tr>
 * <tr><td>PUT</td>    <td><i>N/A</i></td></tr>
 * <tr><td>POST</td>   <td>Create from given description, no ID; ID is generated and URI returned.</td></tr>
 * <tr><td>DELETE</td> <td><i>N/A</i></td></tr>
 * </table>
 * </p>
 * 
 * @author dbacon
 *
 */
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

		{
			String requestedMediaType = (String) getRequest().getAttributes().get("mediatype");
			if (null != requestedMediaType)
			{
				mediaType = MediaType.valueOf(requestedMediaType);
			}
			
			log.info("rendering items for " + variant.getMediaType());
		}
		
		List<StartTag> tags = new LinkedList<StartTag>(getMyApp().getStartTags().values());

		tags = new Split().split(tags, new Instant());
		
		{
			Comparator<StartTag> cmp = CompareByTime.Instance;
			
			String sortColumn = (String) getRequest().getAttributes().get("sortdir");
			if (null != sortColumn && "desc".equals(sortColumn))
			{
				cmp = Collections.reverseOrder(cmp);
			}
			
			Collections.sort(tags, cmp);
		}

		{
			String max = (String) getRequest().getAttributes().get("max");
			if (null != max)
			{
				tags = tags.subList(0, Math.min(tags.size(), Integer.valueOf(max)));
			}
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
		StartTag startTag = new StartTagHelper().parseEntity(entity, getResponse());
		
		if (null != startTag)
		{
			if (getMyApp().getStartTags().containsKey(startTag.getWhen()))
			{
				getResponse().setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
			}
			else
			{
				// TODO: better integration/validation support upon adding startTag to list.
				//  for example collisions, missing untils if in past, etc.
				getMyApp().getStartTags().put(startTag.getWhen(), startTag);
				
				getResponse().setStatus(Status.SUCCESS_CREATED, "created.");
				getResponse().redirectSeeOther("/items");
			}
		}
	}

	@Override
	public boolean allowPut()
	{
		return false;
	}
}
