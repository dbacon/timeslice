package bacond.timeslicer.svc.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
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

import bacond.lib.restlet.BulletedListTextHtmlRenderer;
import bacond.lib.restlet.FormattedStringTextPlainRenderer;
import bacond.lib.restlet.HtmlPagifier;
import bacond.lib.restlet.JsonArrayRenderer;
import bacond.lib.restlet.ToStringRepr;
import bacond.lib.util.ITransform;
import bacond.lib.util.MapMaker;
import bacond.lib.util.Transforms;
import bacond.timeslicer.app.processing.Aggregate;
import bacond.timeslicer.app.processing.Split;
import bacond.timeslicer.app.task.StartTag;
import bacond.timeslicer.app.tasktotal.TaskTotal;
import bacond.timeslicer.restletservice.MyApp;
import bacond.timeslicer.svc.tasktotal.TextPlainTaskTotalsFormatter;

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
	private final Map<MediaType, ITransform<Collection<StartTag>, Representation>> startTagRenderers = MapMaker.create(new LinkedHashMap<MediaType, ITransform<Collection<StartTag>, Representation>>())
		.put(MediaType.TEXT_PLAIN, FormattedStringTextPlainRenderer.create(new TextPlainStartTagsFormatter()))
		.put(MediaType.TEXT_HTML,
				ToStringRepr.create(MediaType.TEXT_HTML,
						HtmlPagifier.pagify(BulletedListTextHtmlRenderer.create(new ToString("[<small><a href=\"/items/%2$s\">%2$s</a></small>] %3$s", 9)))))
		.put(MediaType.APPLICATION_JSON, new JsonArrayRenderer<StartTag>())
		.getMap();

	private final Map<MediaType, ITransform<Collection<TaskTotal>, Representation>> taskTotalRenderers = MapMaker.create(new LinkedHashMap<MediaType, ITransform<Collection<TaskTotal>, Representation>>())
		.put(MediaType.TEXT_PLAIN, FormattedStringTextPlainRenderer.create(new TextPlainTaskTotalsFormatter()))
		.put(MediaType.APPLICATION_JSON, new JsonArrayRenderer<TaskTotal>())
		.getMap();

	protected Map<MediaType, ITransform<Collection<StartTag>, Representation>> getStartTagRenderers()
	{
		return startTagRenderers;
	}

	protected Map<MediaType, ITransform<Collection<TaskTotal>, Representation>> getTaskTotalRenderers()
	{
		return taskTotalRenderers;
	}

	public StartTagsResource(Context context, Request request, Response response)
	{
		super(context, request, response);

		for (MediaType mediaType: getStartTagRenderers().keySet())
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
		public static final String Processing = "proctype";

		@Deprecated
		public static final String Enrich = "enrich";

		public static final String Download = "download";
		public static final String Snapshot = "snapshot";

	}

	public static final List<String> QueryParamNameList = Arrays.asList(
			QueryParamNames.SortDir,
			QueryParamNames.PageSize,
			QueryParamNames.PageIndex,
			QueryParamNames.Enrich,
			QueryParamNames.MediaTypeOverride,
			QueryParamNames.MinTime,
			QueryParamNames.MaxTime,
			QueryParamNames.Processing,
			QueryParamNames.Snapshot,
			QueryParamNames.Download);

	public static Instant parseInstantIfAvailable(String a)
	{
		if (null == a || a.trim().isEmpty())
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

	public static String decode(String p)
	{
		if (null == p)
		{
			return null;
		}
		else
		{
			try
			{
				return URLDecoder.decode(p, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException("Could not decode: " + e.getMessage(), e);
			}
		}
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		MediaType mediaType = Transforms.mapNullTo(parseMediaTypeIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.MediaTypeOverride)), variant.getMediaType());

		Boolean sortReverse = "desc".equals(getRequest().getAttributes().get(QueryParamNames.SortDir));
		Instant minDate = Transforms.mapNullTo(parseInstantIfAvailable(decode((String) getRequest().getAttributes().get(QueryParamNames.MinTime))), new Instant(0));
		Instant maxDate = Transforms.mapNullTo(parseInstantIfAvailable(decode((String) getRequest().getAttributes().get(QueryParamNames.MaxTime))), new Instant(Long.MAX_VALUE));
		Integer pageSize = Transforms.mapNullTo(parseIntegerIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.PageSize)), Integer.MAX_VALUE);
		Integer pageIndex = Transforms.mapNullTo(parseIntegerIfAvailable((String) getRequest().getAttributes().get(QueryParamNames.PageIndex)), 0);
		String processing = Transforms.mapNullTo((String) getRequest().getAttributes().get(QueryParamNames.Processing), "none");
		String downloadName = (String) getRequest().getAttributes().get(QueryParamNames.Download);
		String snapshot = (String) getRequest().getAttributes().get(QueryParamNames.Snapshot);

		processSnapshotRequest(snapshot);

		List<StartTag> tags = new Split().split(
				getMyApp().getStartTagStore().getItemsConstrained(
					minDate,
					maxDate,
					sortReverse,
					pageSize,
					pageIndex),
				new Instant());

		if ("sumbydesc".equals(processing))
		{
			return render(mediaType, getTaskTotalRenderers(), new Aggregate().sumThem(new Aggregate().aggregate(tags)).values(), downloadName);
		}
		else
		{
			return render(mediaType, getStartTagRenderers(), tags, downloadName);
		}
	}

	private void processSnapshotRequest(String snapshot)
	{
		if (null != snapshot)
		{
			try
			{
				getMyApp().snapshot(snapshot);
			}
			catch (Exception e)
			{
				System.err.println("Could not write snapshot: " + e.getMessage());
			}
		}
	}

	private <T> Representation render(MediaType mediaType, Map<MediaType, ITransform<T, Representation>> rendererMap, T tags, String downloadName)
	{
		ITransform<T, Representation> renderer = rendererMap.get(mediaType);

		if (null != renderer)
		{
			Representation repr = renderer.apply(tags);

			if (null != downloadName)
			{
				repr.setDownloadable(true);
				repr.setDownloadName(downloadName);
			}

			return repr;
		}
		else
		{
			getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
			return null;
		}
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
			if (getMyApp().getStartTagStore().contains(startTag.getWhen()))
			{
				rollback = true;
				break;
			}
			else
			{
				// TODO: better integration/validation support upon adding startTag to list.
				//  for example collisions, missing untils if in past, etc.

				getMyApp().getStartTagStore().enterTag(startTag);
			}
		}

		if (rollback)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
		}
		else
		{
			getResponse().setStatus(Status.SUCCESS_CREATED, "created.");
			getResponse().redirectSeeOther(Transforms.mapNullTo((String) getResponse().getAttributes().get("redirect"), ""));
		}
	}

	@Override
	public boolean allowPut()
	{
		return false;
	}
}
