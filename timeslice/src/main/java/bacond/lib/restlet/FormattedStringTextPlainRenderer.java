/**
 * 
 */
package bacond.lib.restlet;

import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;

import bacond.lib.util.ITransform;

public class FormattedStringTextPlainRenderer<T> implements IRenderer<T>
{
	private final ITransform<T, String> formatter;

	public FormattedStringTextPlainRenderer(ITransform<T, String> formatter)
	{
		this.formatter = formatter;
	}
	
	@Override
	public Representation render(T t)
	{
		return new StringRepresentation(formatter.apply(t));
	}
}