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

	public static <T> T disallowNull(T t)
	{
		return disallowNull(t, null);
	}

	public static <T> T disallowNull(T t, String name)
	{
		if (null == t)
		{
			throw new RuntimeException("Null not allowed" + ((null == name) ? "" : (" for '" + name + "'")) + ".");
		}

		return t;
	}
}
