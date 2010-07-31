package bacond.timeslice.web.gwt.server.beantx;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.core.StartTag;

public class ServerToClient
{
    public static ITransform<StartTag, bacond.timeslice.web.gwt.client.beans.StartTag> createStartTagTx(final int tzoffset)
    {
        return new ITransform<StartTag, bacond.timeslice.web.gwt.client.beans.StartTag>()
        {
            @Override
            public bacond.timeslice.web.gwt.client.beans.StartTag apply(StartTag r)
            {
                return new bacond.timeslice.web.gwt.client.beans.StartTag(
                        r.getWhen().toDateTime(DateTimeZone.forOffsetHours(tzoffset)).toString(),
                        r.getUntil() == null ? null : r.getUntil().toString(),
                        r.getUntil() == null ? null : new Double(new Duration(r.getWhen(), r.getUntil()).toDuration().getMillis()),
                        r.getWhat(),
                        r.getWhen().isBeforeNow()
                        );
            }
        };
    }
}
