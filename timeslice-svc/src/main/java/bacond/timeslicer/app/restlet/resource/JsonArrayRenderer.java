package bacond.timeslicer.app.restlet.resource;

import java.util.Collection;

import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;

import bacond.lib.restlet.IRenderer;

public class JsonArrayRenderer<T> implements IRenderer<Collection<T>>
{
	@Override
	public Representation apply(Collection<T> ts)
	{
		return new JsonRepresentation(new JSONArray(ts));
	}
}