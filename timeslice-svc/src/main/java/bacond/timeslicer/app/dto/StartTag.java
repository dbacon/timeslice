package bacond.timeslicer.app.dto;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
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
	
	@Override
	public String toString()
	{
		return String.format("[%s, %s, %s, %s]", getWho(), getWhen(), getUntil(), getWhat());
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
		int tzOffsetInHours = 9; // TODO: figure out how to expose/delegate/workaround this.
		
		DateTimeFormatter withZone = ISODateTimeFormat.dateTime().withZone(DateTimeZone.forOffsetHours(tzOffsetInHours));
		
		try
		{
			return new JSONStringer()
				.object()
					.key("when")
					.value(withZone.print(getWhen()))
					.key("what")
					.value(getWhat())
					.key("until")
					.value(null != getUntil() ? withZone.print(getUntil()) : null)
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
