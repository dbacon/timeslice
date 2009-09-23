package bacond.lib.restlet;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;


public class JsonObjectRenderer<T> implements IRenderer<T>
{
	@Override
	public Representation apply(T t)
	{
		return new JsonRepresentation(t);
	}
}
