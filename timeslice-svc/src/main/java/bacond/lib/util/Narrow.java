package bacond.lib.util;

import java.util.ArrayList;
import java.util.List;

public class Narrow
{
	@SuppressWarnings("unchecked")
	public static <T> T castSingle(Object o)
	{
		return (T) o;
	}
	
	public static <T> List<T> fromList(List<?> list)
	{
		ArrayList<T> result = new ArrayList<T>(list.size());

		for (Object o: list)
		{
			result.add(Narrow.<T>castSingle(o));
		}
		
		return result;
	}
}
