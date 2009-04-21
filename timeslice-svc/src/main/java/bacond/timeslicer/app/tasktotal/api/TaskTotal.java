package bacond.timeslicer.app.tasktotal.api;

import org.json.JSONString;
import org.json.JSONStringer;

public class TaskTotal implements JSONString
{
	private final String who;
	private final int millis;
	private final String what;

	public TaskTotal(String who, int millis, String what)
	{
		this.who = who;
		this.millis = millis;
		this.what = what;
	}

	@Override
	public String toString()
	{
		return String.format("[%s, %d, %s]", getWho(), getMillis(), getWhat());
	}

	public String getWho()
	{
		return who;
	}

	public int getMillis()
	{
		return millis;
	}

	public String getWhat()
	{
		return what;
	}

	@Override
	public String toJSONString()
	{
		try
		{
			return new JSONStringer()
				.object()

					.key("who")
					.value(getWho())

					.key("durationms")
					.value(getMillis())

					.key("what")
					.value(getWhat())

				.endObject()
				.toString();

		}
		catch (Exception e)
		{
			throw new RuntimeException("JSON representation failed: " + e.getMessage(), e);
		}
	}
}
