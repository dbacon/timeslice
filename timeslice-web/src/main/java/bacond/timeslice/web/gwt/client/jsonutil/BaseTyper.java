package bacond.timeslice.web.gwt.client.jsonutil;

public class BaseTyper<T>
{
	protected T throwIfNull(T t)
	{
		if (null == t)
		{
			throw new RuntimeException("");
		}
		
		return t;
	}
}