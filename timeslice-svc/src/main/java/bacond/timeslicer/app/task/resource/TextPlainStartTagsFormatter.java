package bacond.timeslicer.app.task.resource;

import java.util.Collection;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.task.api.StartTag;

public final class TextPlainStartTagsFormatter implements ITransform<Collection<StartTag>, String>
{
	@Override
	public String apply(Collection<StartTag> r)
	{
		StringBuilder sb = new StringBuilder("[").append("\n");

		for (StartTag startTag: r)
		{
			sb.append(new ToString("[%1$s#%2$s#%3$s]", 9).apply(startTag)).append("\n");
		}

		return sb.append("]").toString();
	}
}