package bacond.timeslicer.app.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import bacond.lib.restlet.FormattedStringTextPlainRenderer;
import bacond.lib.restlet.IRenderer;
import bacond.timeslicer.app.dto.Item;

public class ItemResource extends Resource
{
	private static final Logger log = Logger.getLogger(ItemResource.class.getCanonicalName());
	
	private final Map<MediaType, IRenderer<Item>> renderers = new LinkedHashMap<MediaType, IRenderer<Item>>();
	
	public ItemResource(Context context, Request request, Response response)
	{
		super(context, request, response);
		
		renderers.put(new MediaType("text/plain"), new FormattedStringTextPlainRenderer<Item>(TextPlainItemFormatter.Instance));
		renderers.put(new MediaType("text/html"), new TextHtmlItemRenderer("<b>%s</b> <i>(%s)</i>"));

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

		Item item = getMyApp().getItems().get(key);
		
		if (null == item)
		{
			return null;
		}

		IRenderer<Item> renderer = renderers.get(variant.getMediaType());
		
		if (null == renderer)
		{
			return null;
		}
		
		return renderer.render(item);
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
