package com.enokinomi.timeslice.web.gwt.server.task;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.enokinomi.timeslice.app.core.StartTag;
import com.enokinomi.timeslice.lib.util.ITransform;


public class ServerToClient
{
    public static ITransform<StartTag, com.enokinomi.timeslice.web.gwt.client.task.core.StartTag> createStartTagTx(final int tzoffset)
    {
        return new ITransform<StartTag, com.enokinomi.timeslice.web.gwt.client.task.core.StartTag>()
        {
            @Override
            public com.enokinomi.timeslice.web.gwt.client.task.core.StartTag apply(StartTag r)
            {
                return new com.enokinomi.timeslice.web.gwt.client.task.core.StartTag(
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
