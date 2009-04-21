package bacond.timeslicer.app.generic;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

import freemarker.template.Configuration;

public abstract class ListableResource<T extends IListable & IHasWhen> extends Resource
{
	private T item;

	public abstract IStoreProvider<T> getStoreProvider();

	public ListableResource(Context context, Request request, Response response)
	{
		super(context, request, response);

		setModifiable(false);

		getVariants().add(new Variant(MediaType.TEXT_PLAIN));
	}

	public T getItem()
	{
		return item;
	}

	public void setItem(T item)
	{
		this.item = item;
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		return new TemplateRepresentation("listable.fmt", findFreemarkerConfig(), new Listing<T>("home", getStoreProvider().getStore().getAllItems()), MediaType.TEXT_HTML);
	}

	private Configuration findFreemarkerConfig()
	{
		Configuration config = new Configuration();
		config.setClassForTemplateLoading(ListableResource.class, "/templates");
		return config;
	}
}
