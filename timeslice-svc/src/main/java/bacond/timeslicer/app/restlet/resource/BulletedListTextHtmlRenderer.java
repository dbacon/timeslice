package bacond.timeslicer.app.restlet.resource;

import java.util.Collection;

import bacond.lib.util.ITransform;

public class BulletedListTextHtmlRenderer<T> implements ITransform<Collection<T>, String>
{
	private final ITransform<T, String> formatter;
	
	public static <T> BulletedListTextHtmlRenderer<T> create(ITransform<T, String> formatter)
	{
		return new BulletedListTextHtmlRenderer<T>(formatter);
	}

	public BulletedListTextHtmlRenderer(ITransform<T, String> formatter)
	{
		this.formatter = formatter;
	}
	
	@Override
	public String apply(Collection<T> items)
	{
		StringBuilder sb = new StringBuilder("<ul>");
		
		for (T item: items)
		{
			sb
				.append("<li>")
				.append(formatter.apply(item))
				.append("</li>");
		}
		
		return sb.append("</ul>").toString();
	}

}
