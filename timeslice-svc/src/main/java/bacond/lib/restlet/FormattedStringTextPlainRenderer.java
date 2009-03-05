package bacond.lib.restlet;

import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;

import bacond.lib.util.ITransform;

public class FormattedStringTextPlainRenderer<T> implements IRenderer<T>
{
	private final ITransform<T, String> formatter;

	public static <T> FormattedStringTextPlainRenderer<T> create(ITransform<T, String> formatter)
	{
		return new FormattedStringTextPlainRenderer<T>(formatter);
	}
	
	public FormattedStringTextPlainRenderer(ITransform<T, String> formatter)
	{
		this.formatter = formatter;
	}
	
	@Override
	public Representation apply(T t)
	{
		return new StringRepresentation(formatter.apply(t));
	}
}