package bacond.timeslicer.app.resource;

import java.util.Collection;

import org.restlet.data.MediaType;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;

import bacond.lib.restlet.IRenderer;
import bacond.timeslicer.app.dto.Item;

public class TextHtmItemslRenderer implements IRenderer<Collection<Item>>
{
	private final String formatSpecifier;

	public TextHtmItemslRenderer(String formatSpecifier)
	{
		this.formatSpecifier = formatSpecifier;
	}
	
	@Override
	public Representation render(Collection<Item> items)
	{
		StringBuilder sb = new StringBuilder("<html><body><ul>");
		
		for (Item item: items)
		{
			sb
				.append("<li>")
				.append(String.format(formatSpecifier, item.getKey(), item.getProject()))
				.append("</li>");
		}
		
		sb.append("</ul></body></html>");
		
		StringRepresentation representation = new StringRepresentation(sb.toString());
		representation.setMediaType(MediaType.TEXT_HTML);
		return representation;
	}

}
