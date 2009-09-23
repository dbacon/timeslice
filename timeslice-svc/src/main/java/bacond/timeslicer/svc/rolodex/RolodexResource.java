package bacond.timeslicer.svc.rolodex;

import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import bacond.timeslicer.app.rolodex.ClientInfo;
import bacond.timeslicer.app.rolodex.IRolodex;
import bacond.timeslicer.restletservice.MyApp;
import bacond.timeslicer.timeslice.TimesliceApp;

public class RolodexResource extends ServerResource
{
	public RolodexResource()
	{
		super();
	}

	protected MyApp getMyApp()
	{
		return (MyApp) getApplication();
	}

	protected TimesliceApp getTimesliceApp()
	{
		return getMyApp().getTimesliceApp();
	}

	protected IRolodex getRolodex()
	{
		return getTimesliceApp().getRolodex();
	}

	@Get()
	public Representation toXml()
	{
		try
		{
			DomRepresentation rep = new DomRepresentation(MediaType.TEXT_XML);

			new RolodexDomifier().populateDoc(getRolodex(), rep.getDocument());

			return rep;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	@Post
	public Representation acceptRepresentation(Representation representation)
	{
		if (MediaType.TEXT_XML.equals(representation.getMediaType()))
		{
			return new StringRepresentation("Accepted XML");
		}
		else if (MediaType.APPLICATION_WWW_FORM.equals(representation.getMediaType()))
		{
			Form form = new Form(representation);
			String content = form.getFirstValue("bulkcontent");
			String format = form.getFirstValue("format");

			if (null == content)
			{
				throw new RuntimeException("Required form parameter 'bulkcontent' is missing.");
			}

			if (null == format)
			{
				format = "xml";
			}

			if ("text1".equals(format))
			{
				getRolodex().addClientInfo(new ClientInfo(content));
			}

			// TODO: parse 'content' into an

			return new StringRepresentation("Accepted form with " + format + " content.");
		}
		throw new RuntimeException("died.");

	}
}
