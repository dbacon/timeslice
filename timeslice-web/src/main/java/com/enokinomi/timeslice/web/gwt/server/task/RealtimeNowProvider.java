package com.enokinomi.timeslice.web.gwt.server.task;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.app.assign.INowProvider;

public class RealtimeNowProvider implements INowProvider
{
    @Override
    public DateTime getNow()
    {
        return new DateTime();
    }

}
