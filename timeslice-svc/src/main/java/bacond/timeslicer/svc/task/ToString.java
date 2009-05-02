package bacond.timeslicer.svc.task;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.task.StartTag;

public class ToString implements ITransform<StartTag, String>
{
	private final String formatSpecifier;
	private final DateTimeFormatter withZone;

	/**
	 * Args 1: who, 2: when, 3: what.
	 *
	 * @param formatSpecifier
	 * @param tzOffsetInHours
	 */
	public ToString(String formatSpecifier, int tzOffsetInHours)
	{
		this.formatSpecifier = formatSpecifier;
		this.withZone = ISODateTimeFormat.dateTime().withZone(DateTimeZone.forOffsetHours(tzOffsetInHours));
	}

	@Override
	public String apply(StartTag startTag)
	{
		return String.format(formatSpecifier,
				startTag.getWho(),
				withZone.print(startTag.getWhen()),
				startTag.getWhat());
	}
}