/**
 * 
 */
package bacond.timeslicer.app.resource;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;

import bacond.lib.restlet.IRenderer;

public class JSonObjectRenderer<T> implements IRenderer<T>
{
	@Override
	public Representation render(T t)
	{
		return new JsonRepresentation(t);
	}
}