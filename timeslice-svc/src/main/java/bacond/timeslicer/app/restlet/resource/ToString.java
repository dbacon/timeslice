package bacond.timeslicer.app.restlet.resource;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.dto.StartTag;

public class ToString implements ITransform<StartTag, String>
{
	private final String formatSpecifier;

	public ToString(String formatSpecifier)
	{
		this.formatSpecifier = formatSpecifier;
	}
	
	@Override
	public String apply(StartTag startTag)
	{
		return String.format(formatSpecifier, startTag.getWhen(), startTag.getWhat());
	}
}