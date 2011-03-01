package com.enokinomi.timeslice.web.report.server.impl;

import com.enokinomi.timeslice.lib.task.api.TaskTotalMember;
import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.web.assign.client.core.TaskTotal;


public class ServerToClient
{
    ServerToClient()
    {
    }

    public static ITransform<TaskTotalMember, TaskTotal> createTaskTotal(final int tzOffsetMinutes)
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
