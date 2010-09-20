package com.enokinomi.timeslice.lib.assign;

import org.joda.time.DateTime;


public class RealtimeNowProvider implements INowProvider
{
    @Override
    public DateTime getNow()
    {
        return new DateTime();
    }

}
