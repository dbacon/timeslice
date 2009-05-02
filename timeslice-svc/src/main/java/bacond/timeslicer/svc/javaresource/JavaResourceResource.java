package bacond.timeslicer.svc.javaresource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

public class JavaResourceResource extends Resource
{
	private final String resourceBaseName;

	public JavaResourceResource()
	{
		this("/public");
	}

	public JavaResourceResource(String resourceBaseName)
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
				InputStream resourceStream = JavaResourceResource.class.getResourceAsStream(getResourceBaseName() + "/" + resourcePath);
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
