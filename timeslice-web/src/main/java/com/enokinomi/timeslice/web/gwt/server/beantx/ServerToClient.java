package com.enokinomi.timeslice.web.gwt.server.beantx;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.enokinomi.timeslice.app.core.StartTag;
import com.enokinomi.timeslice.lib.util.ITransform;


public class ServerToClient
{
    public static ITransform<StartTag, com.enokinomi.timeslice.web.gwt.client.beans.StartTag> createStartTagTx(final int tzoffset)
    {
        return new ITransform<StartTag, com.enokinomi.timeslice.web.gwt.client.beans.StartTag>()
        {
            @Override
            public com.enokinomi.timeslice.web.gwt.client.beans.StartTag apply(StartTag r)
            {
                return new com.enokinomi.timeslice.web.gwt.client.beans.StartTag(
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
