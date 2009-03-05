/**
 * 
 */
package bacond.timeslicer.app.resource;

import java.util.Collection;

import org.json.JSONArray;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;

import bacond.lib.restlet.IRenderer;

public class JSonArrayRenderer<T> implements IRenderer<Collection<T>>
{
	@Override
	public Representation render(Collection<T> ts)
	{
		return new JsonRepresentation(new JSONArray(ts));
	}
}