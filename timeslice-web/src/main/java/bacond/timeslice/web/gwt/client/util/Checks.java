package bacond.timeslice.web.gwt.client.util;

public class Checks
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
