package bacond.timeslicer.app.restlet.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

public class ResourceResource extends Resource
{
	private final String resourceBaseName;

	public ResourceResource()
	{
		this("/public");
	}

	public ResourceResource(String resourceBaseName)
	{
		this.resourceBaseName = resourceBaseName;

		setModifiable(false);
		setReadable(true);
		getVariants().add(new Variant(MediaType.TEXT_PLAIN));
	}
	
	public String getResourceBaseName()
	{
		return resourceBaseName;
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		Representation result = null;
		
		if (MediaType.TEXT_PLAIN.equals(variant.getMediaType()))
		{
			String resourcePath = (String) getRequest().getAttributes().get("resPath");
			
			if (null != resourcePath)
			{
				InputStream resourceStream = ResourceResource.class.getResourceAsStream(getResourceBaseName() + "/" + resourcePath);
				if (null != resourceStream)
				{
					try
					{
						result = new StringRepresentation(IOUtils.toString(resourceStream));
					}
					catch (IOException e)
					{
						getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
					}
				}
				else
				{
					getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				}
			}
			else
			{
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No resource path given.");
			}
		}
		else
		{
			getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Only text/plain is supported.");
		}

		return result;
	}

}
