package bacond.timeslicer.web.gwt.client.util;

public class Transforms
{
	public static <T> T mapNullTo(T t, T altT)
	{
		if (null == t)
		{
			return altT;
		}
		else
		{
			return t;
		}
	}
	
}
