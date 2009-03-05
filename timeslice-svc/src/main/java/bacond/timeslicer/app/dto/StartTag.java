package bacond.timeslicer.app.dto;

import org.joda.time.Instant;
import org.joda.time.Interval;
import org.json.JSONString;
import org.json.JSONStringer;

public class StartTag implements JSONString
{
	private final String who;
	private final Instant when;
	private final String what;
	private final Instant until;
	
	public StartTag(String who, Instant when, String what, Instant until)
	{
		this.who = who;
		this.when = when;
		this.what = what;
		this.until = until;
	}

	public String getWho()
	{
		return who;
	}

	public Instant getWhen()
	{
		return when;
	}

	public String getWhat()
	{
		return what;
	}

	public Instant getUntil()
	{
		return until;
	}
	
	@Override
	public String toJSONString()
	{
		try
		{
			return new JSONStringer()
				.object()
					.key("when")
					.value(getWhen())
					.key("what")
					.value(getWhat())
					.key("until")
					.value(getUntil())
					.key("durationms")
					.value(null != getUntil() ? new Interval(getWhen(), getUntil()).toDurationMillis() : null)
				.endObject()
				.toString();

		}
		catch (Exception e)
		{
			throw new RuntimeException("JSON representation failed: " + e.getMessage(), e);
		}
	}
}
