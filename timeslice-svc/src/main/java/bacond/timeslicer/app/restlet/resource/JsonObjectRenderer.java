/**
 * 
 */
package bacond.timeslicer.app.restlet.resource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;

import bacond.lib.restlet.IRenderer;

public class JsonObjectRenderer<T> implements IRenderer<T>
{
	@Override
	public Representation apply(T t)
	{
		return new JsonRepresentation(t);
	}
}