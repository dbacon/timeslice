package bacond.timeslicer.app.resource;

import org.restlet.data.MediaType;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;

import bacond.lib.restlet.IRenderer;
import bacond.timeslicer.app.dto.Item;

public class TextHtmlItemRenderer implements IRenderer<Item>
{
	private final String formatSpecifier;

	public TextHtmlItemRenderer(String formatSpecifier)
	{
		this.formatSpecifier = formatSpecifier;
	}
	
	@Override
	public Representation render(Item item)
	{
		StringBuilder sb = new StringBuilder("<html><body>");
		
		sb.append(String.format(formatSpecifier, item.getKey(), item.getProject()));
		
		sb.append("</body></html>");
		
		StringRepresentation representation = new StringRepresentation(sb.toString());
		representation.setMediaType(MediaType.TEXT_HTML);
		return representation;
	}

}
