package bacond.timeslicer.app.restlet.resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
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
import bacond.lib.util.Transforms;
import bacond.timeslicer.app.dto.StartTag;

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

	public static class QueryParamNames
	{
		/*
		 *  web service ones
		 */
		
		public static final String MediaTypeOverride = "mediatypeoverride";
		
		/*
		 *  application ones
		 */
		public static final String MinTime = "mintime";
		public static final String MaxTime = "maxtime";
		public static final String SortDir = "sortdir";
		public static final String PageSize = "pagesize";
		public static final String PageIndex = "pageindex";

		@Deprecated
		public static final String Enrich = "enrich";
	
	}
	
	public static final List<String> QueryParamNameList = Arrays.asList(
			QueryParamNames.SortDir,
			QueryParamNames.PageSize,
			QueryParamNames.PageIndex,
			QueryParamNames.Enrich,
			QueryParamNames.MediaTypeOverride,
			QueryParamNames.MinTime,
			QueryParamNames.MaxTime);
	
	public static Instant parseInstantIfAvailable(String a)
	{
		if (null == a)
		{
			return null;
		}
		else
		{
			return ISODateTimeFormat.dateTime().parseDateTime(a).toInstant();
		}
	}

	public Integer parseIntegerIfAvailable(String a)
	{
		if (null == a)
		{
			return null;
		}
		else
		{
			return Integer.valueOf(a);
		}
	}
	
	public MediaType parseMediaTypeIfAvailable(String a)
	{
		if (null == a)
		{
			return null;
		}
		else
		{
			return MediaType.valueOf(a);
		}
	}
	
	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		MediaType mediaType = Transforms.mapNullTo(parseMediaTypeIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.MediaTypeOverride)), variant.getMediaType());
		
		Boolean sortReverse = "desc".equals((String) getRequest().getAttributes().get(QueryParamNames.SortDir));
		Instant minDate = Transforms.mapNullTo(parseInstantIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.MinTime)), new Instant(0));
		Instant maxDate = Transforms.mapNullTo(parseInstantIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.MaxTime)), new Instant(Long.MAX_VALUE));
		Integer pageSize = Transforms.mapNullTo(parseIntegerIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.PageSize)), Integer.MAX_VALUE);
		Integer pageIndex = Transforms.mapNullTo(parseIntegerIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.PageIndex)), 0);
		
		List<StartTag> tags = new LinkedList<StartTag>(getMyApp().getMeSomeTags(
				minDate,
				maxDate,
				sortReverse,
				pageSize,
				pageIndex));

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
		List<StartTag> startTags = new StartTagHelper().parseEntity(entity, getResponse());
		boolean rollback = false;
		
		for (StartTag startTag: startTags)
		{
			if (getMyApp().getStartTags().containsKey(startTag.getWhen()))
			{
				rollback = true;
				break;
			}
			else
			{
				// TODO: better integration/validation support upon adding startTag to list.
				//  for example collisions, missing untils if in past, etc.
				
				getMyApp().getStartTags().put(startTag.getWhen(), startTag);
			}
		}

		if (rollback)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
		}
		else
		{
			getResponse().setStatus(Status.SUCCESS_CREATED, "created.");
			getResponse().redirectSeeOther("/items");
		}
	}

	@Override
	public boolean allowPut()
	{
		return false;
	}
}
