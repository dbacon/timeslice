package bacond.timeslicer.app.restlet.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import bacond.lib.restlet.FormattedStringTextPlainRenderer;
import bacond.lib.util.ITransform;
import bacond.lib.util.MapMaker;
import bacond.lib.util.Transforms;
import bacond.timeslicer.app.dto.StartTag;

public class StartTagResource extends Resource
{
	private static final Logger log = Logger.getLogger(StartTagResource.class.getCanonicalName());
	
	private final Map<MediaType, ITransform<StartTag, Representation>> renderers = MapMaker.create(new LinkedHashMap<MediaType, ITransform<StartTag, Representation>>())
			.put(MediaType.APPLICATION_JSON, new JsonObjectRenderer<StartTag>())
			.put(MediaType.TEXT_PLAIN, new FormattedStringTextPlainRenderer<StartTag>(TextPlainStartTagFormatter.Instance))
			.put(MediaType.TEXT_HTML, Transforms.compose(new ToString("<b>%s</b> <i>(%s)</i>"), new ToStringRepr(MediaType.TEXT_HTML)))
			.getMap();
	
	public StartTagResource(Context context, Request request, Response response)
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
		log.info("represent");
		
		String key = (String) getRequest().getAttributes().get("itemId");
		
		if (null == key)
		{
			return null;
		}
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		
		StartTag startTag = getMyApp().getStartTags().get(new Instant(fmt.parseMillis(key)));
		
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
	public void acceptRepresentation(Representation entity) throws ResourceException
	{
		log.info("acceptRepresentation");
		super.acceptRepresentation(entity);
	}

	@Override
	public void storeRepresentation(Representation entity) throws ResourceException
	{
		log.info("storeRepresentation");
		super.storeRepresentation(entity);
	}
}
