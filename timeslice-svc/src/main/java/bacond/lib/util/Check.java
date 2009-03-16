package bacond.lib.util;

public class Check
{
	public static void notNull(Object o, String s)
	{
		if (null == o)
		{
			throw new RuntimeException("Object must not be null: " + s);
		}
	}
}
