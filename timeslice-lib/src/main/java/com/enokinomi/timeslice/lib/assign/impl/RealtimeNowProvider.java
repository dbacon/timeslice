package com.enokinomi.timeslice.lib.assign.impl;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.lib.assign.api.INowProvider;


public class RealtimeNowProvider implements INowProvider
{
    @Override
    public DateTime getNow()
    {
        return new DateTime();
    }

}
