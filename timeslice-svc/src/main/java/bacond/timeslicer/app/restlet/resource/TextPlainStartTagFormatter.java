package bacond.timeslicer.app.restlet.resource;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.dto.StartTag;

public final class TextPlainStartTagFormatter implements ITransform<StartTag, String>
{
	public static final TextPlainStartTagFormatter Instance = new TextPlainStartTagFormatter();
	
	@Override
	public String apply(StartTag startTag)
	{
		return new StringBuilder()
			.append("[")
			.append(startTag.getWho())
			.append("#")
			.append(startTag.getWhen())
			.append("#")
			.append(startTag.getWhat())
			.append("]")
			.toString();
	}
}