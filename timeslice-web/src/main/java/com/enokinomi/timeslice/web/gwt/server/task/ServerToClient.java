package com.enokinomi.timeslice.web.gwt.server.task;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.enokinomi.timeslice.lib.task.StartTag;
import com.enokinomi.timeslice.lib.task.TaskTotalMember;
import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.web.gwt.client.task.core.TaskTotal;


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

    public static ITransform<TaskTotalMember, TaskTotal> createTaskTotal(final int tzoffset)
    {
        return new ITransform<TaskTotalMember, TaskTotal>()
        {
            @Override
            public TaskTotal apply(TaskTotalMember r)
            {
                return new TaskTotal(
                        r.getWho(),
                        r.getMillis() / 1000. / 60. / 60.,
                        r.getPercentage(),
                        r.getWhat());
            }
        };
    }
}
