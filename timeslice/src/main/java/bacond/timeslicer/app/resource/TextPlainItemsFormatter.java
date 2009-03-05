/**
 * 
 */
package bacond.timeslicer.app.resource;

import java.util.Collection;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.dto.Item;

public final class TextPlainItemsFormatter implements ITransform<Collection<Item>, String>
{
	public static final TextPlainItemsFormatter Instance = new TextPlainItemsFormatter();

	@Override
	public String apply(Collection<Item> r)
	{
		StringBuilder sb = new StringBuilder("[").append("\n");
		
		for (Item item: r)
		{
			sb.append(TextPlainItemFormatter.Instance.apply(item));
		}
		
		return sb.append("]").toString();
	}
}