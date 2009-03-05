package bacond.lib.restlet;

import org.restlet.resource.Representation;

import bacond.lib.util.ITransform;

public interface IRenderer<T> extends ITransform<T, Representation>
{
}
