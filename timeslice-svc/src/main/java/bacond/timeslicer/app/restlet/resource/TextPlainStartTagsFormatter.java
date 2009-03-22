package bacond.timeslicer.app.restlet.resource;

import java.util.Collection;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.dto.StartTag;

public final class TextPlainStartTagsFormatter implements ITransform<Collection<StartTag>, String>
{
	@Override
	public String apply(Collection<StartTag> r)
	{
		StringBuilder sb = new StringBuilder("[").append("\n");
		
		for (StartTag startTag: r)
		{
			sb.append(new TextPlainStartTagFormatter().apply(startTag)).append("\n");
		}
		
		return sb.append("]").toString();
	}
}