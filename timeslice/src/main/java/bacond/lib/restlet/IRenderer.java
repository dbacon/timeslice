package bacond.lib.restlet;

import org.restlet.resource.Representation;

public interface IRenderer<T>
{
	public Representation render(T t);
}
