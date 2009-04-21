package bacond.lib.restlet;

import org.restlet.representation.Representation;

import bacond.lib.util.ITransform;

public interface IRenderer<T> extends ITransform<T, Representation>
{
}
