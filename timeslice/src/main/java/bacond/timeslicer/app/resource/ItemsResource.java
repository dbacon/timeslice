package bacond.timeslicer.app.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

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
import bacond.lib.restlet.IRenderer;
import bacond.timeslicer.app.dto.Item;

public class ItemsResource extends Resource
{
	private static final Logger log = Logger.getLogger(ItemsResource.class.getCanonicalName());
	
	protected MyApp getMyApp()
	{
		return (MyApp) getApplication();
	}
	
	private final Map<MediaType, IRenderer<Collection<Item>>> renderers = new LinkedHashMap<MediaType, IRenderer<Collection<Item>>>();

	public ItemsResource(Context context, Request request, Response response)
	{
		super(context, request, response);
		
		renderers.put(MediaType.TEXT_PLAIN, new FormattedStringTextPlainRenderer<Collection<Item>>(TextPlainItemsFormatter.Instance));
		renderers.put(MediaType.TEXT_HTML, new TextHtmItemslRenderer("<a href=\"/items/%1$s\">%2$s</a>"));		
		renderers.put(MediaType.APPLICATION_JSON, new JSonArrayRenderer<Item>());		
		
		for (MediaType mediaType: renderers.keySet())
		{
			getVariants().add(new Variant(mediaType));
		}
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		log.info("rendering items for " + variant.getMediaType());
		return renderers.get(variant.getMediaType()).render(getMyApp().getItems().values());
	}

	@Override
	public boolean allowPost()
	{
		return true;
	}

	@Override
	public void acceptRepresentation(Representation entity) throws ResourceException
	{
		String key = null;
		String project = null;
		
		if (entity.getMediaType().equals(MediaType.TEXT_PLAIN))
		{
			log.info("accepting text-plain post");
			
			try
			{
				String reprText = entity.getText();
				
				log.info("reprText: '" + reprText + "'");
				
				String[] pairs = reprText.split(";", -1);
				
				Map<String, String> attrs = new LinkedHashMap<String, String>();
				
				for (String pair: pairs)
				{
					String[] sides = pair.split("=", -1);
					
					String name = sides[0];
					String value = sides[1];
					
					attrs.put(name, value);
				}
				
				key = attrs.get("key");
				project = attrs.get("project");
			}
			catch (IOException e)
			{
				log.info("Couldn't read representation: " + e.getMessage());
			}
		}
		else if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM))
		{
			log.info("accepting form post");
			Form form = new Form(entity);
			key = form.getFirstValue("key");
			project = form.getFirstValue("project");
		}
		else
		{
			log.info("not accepting post; only form and text-plain is acceptable.");
			getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Only form and text-plain is acceptable.");
			return;
		}
		
		if (null == key || null == project)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}
		
		Item item = new Item(key, project);
		
		if (getMyApp().getItems().containsKey(item.getKey()))
		{
			getResponse().setStatus(Status.CLIENT_ERROR_PRECONDITION_FAILED);
			return;
		}
		
		getMyApp().getItems().put(item.getKey(), item);
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
