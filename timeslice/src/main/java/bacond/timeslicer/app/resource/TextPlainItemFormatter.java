/**
 * 
 */
package bacond.timeslicer.app.resource;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.dto.Item;

public final class TextPlainItemFormatter implements ITransform<Item, String>
{
	public static final TextPlainItemFormatter Instance = new TextPlainItemFormatter();
	
	@Override
	public String apply(Item item)
	{
		StringBuilder sb = new StringBuilder("");
		
		sb.append("[").append(item.getKey()).append(":").append(item.getProject()).append("]").append("\n");

		return sb.toString();
	}
}